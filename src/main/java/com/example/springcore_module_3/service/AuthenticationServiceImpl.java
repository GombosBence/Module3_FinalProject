package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dto.request.AuthenticationRequest;
import com.example.springcore_module_3.exception.AuthenticationFailedException;
import com.example.springcore_module_3.exception.UnAuthorizedAccessException;
import com.example.springcore_module_3.model.User;
import com.example.springcore_module_3.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService{

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthenticationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void authenticate(String username, String password) {

        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            log.error("Username {} not found", username);
            return new AuthenticationFailedException("Invalid username or password");
        });

        if(!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Authentication failed, password mismatch for username: {}", username);
            throw new AuthenticationFailedException("Invalid username or password");
        }

        log.debug("Authentication successful for username: {}", username);
    }

    @Override
    public void authenticateAndAuthorize(String username, String password, String targetUsername) {
        authenticate(username, password);

        if(!username.equals(targetUsername)) {
            log.warn("User {} attempted to act on profile {}", username, targetUsername);
            throw new UnAuthorizedAccessException("You are not authorized to perform this operation");
        }
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
