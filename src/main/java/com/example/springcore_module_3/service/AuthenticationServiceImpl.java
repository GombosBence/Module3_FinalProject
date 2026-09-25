package com.example.springcore_module_3.service;

import com.example.springcore_module_3.exception.UserAccountLockedException;
import com.example.springcore_module_3.exception.AuthenticationFailedException;
import com.example.springcore_module_3.metrics.GymMetrics;
import com.example.springcore_module_3.model.User;
import com.example.springcore_module_3.repository.UserRepository;
import com.example.springcore_module_3.util.LoginAttemptTracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService{

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final LoginAttemptTracker loginAttemptTracker;

    private final GymMetrics gymMetrics;

    public AuthenticationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,  GymMetrics gymMetrics,
                                     LoginAttemptTracker loginAttemptTracker) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.gymMetrics = gymMetrics;
        this.loginAttemptTracker = loginAttemptTracker;
    }

    @Override
    public void authenticate(String username, String password) {

        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            log.warn("Username {} not found", username);
            gymMetrics.incrementFailedAuthentications();
            return new AuthenticationFailedException("Invalid username or password");
        });

        if(loginAttemptTracker.isLocked(username)) {
            log.warn("Login attempt blocked, username {} has been locked", username);
            throw new UserAccountLockedException("Account has been locked due to too many failed attempts");
        }

        if(!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Authentication failed, password mismatch for username: {}", username);
            gymMetrics.incrementFailedAuthentications();
            loginAttemptTracker.recordFailure(username);
            throw new AuthenticationFailedException("Invalid username or password");
        }

        if (!user.isActive()) {
            log.warn("Authentication attempted for deactivated username={}", username);
            loginAttemptTracker.recordFailure(username);
            throw new AuthenticationFailedException("Invalid username or password");
        }
        log.debug("Authentication successful for username: {}", username);
        loginAttemptTracker.recordSuccess(username);
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {

        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            log.error("Username  {} not found", username);
            return new AuthenticationFailedException("Invalid username or password");
        });

        if(!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("Password change failed - old password mismatch for username={}", username);
            throw new AuthenticationFailedException("Invalid username or password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password has been successfully changed for username: {}", username);
    }

}
