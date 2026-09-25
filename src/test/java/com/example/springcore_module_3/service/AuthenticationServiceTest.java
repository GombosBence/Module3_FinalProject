package com.example.springcore_module_3.service;

import com.example.springcore_module_3.exception.AuthenticationFailedException;
import com.example.springcore_module_3.exception.UnAuthorizedAccessException;
import com.example.springcore_module_3.metrics.GymMetrics;
import com.example.springcore_module_3.model.User;
import com.example.springcore_module_3.repository.UserRepository;
import com.example.springcore_module_3.util.LoginAttemptTracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private GymMetrics gymMetrics;
    @Mock
    private LoginAttemptTracker loginAttemptTracker;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationServiceImpl(userRepository, passwordEncoder,  gymMetrics, loginAttemptTracker);
    }

    private User generateUser() {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUsername("Test.User");
        user.setPassword("hashedPassword");
        return user;
    }

    @Test
    void successfulAuthenticationTest(){
        User user = generateUser();
        user.setActive(true);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", user.getPassword())).thenReturn(Boolean.TRUE);

        assertDoesNotThrow(() -> authenticationService.authenticate("Test.User", "password"));
    }

    @Test
    void userNameDoesNotExistAuthenticationTest(){
        when(userRepository.findByUsername("Test.User")).thenReturn(Optional.empty());

        assertThrows(AuthenticationFailedException.class, () -> authenticationService.authenticate("Test.User", "password"));
    }

    @Test
    void passwordsDoNotMatchAuthenticationTest(){
        User user = generateUser();
        when(userRepository.findByUsername("Test.User")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", user.getPassword())).thenReturn(false);

        assertThrows(AuthenticationFailedException.class, () -> authenticationService.authenticate("Test.User", "password"));
    }

    @Test
    void authenticate_doesNotRevealWhichCheckFailed() {
        when(userRepository.findByUsername("Nobody.Here")).thenReturn(Optional.empty());
        AuthenticationFailedException unknownUserException = assertThrows(AuthenticationFailedException.class,
                () -> authenticationService.authenticate("Nobody.Here", "anyPassword"));

        User user = generateUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);
        AuthenticationFailedException wrongPasswordException = assertThrows(AuthenticationFailedException.class,
                () -> authenticationService.authenticate(user.getUsername(), "wrongPassword"));

        assertEquals(unknownUserException.getMessage(), wrongPasswordException.getMessage());
    }

    @Test
    void changePassword_successfulAuthenticationTest(){
        User user = generateUser();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        assertDoesNotThrow(() -> authenticationService.changePassword(user.getUsername(), "oldRaw",
                "newPassword"));

        verify(userRepository).save(user);
    }

    @Test
    void changePassword_throwsAuthenticationFailed_whenUsernameDoesNotMatch() {

        when(userRepository.findByUsername("John")).thenReturn(Optional.empty());

        assertThrows(AuthenticationFailedException.class,
                () -> authenticationService.changePassword("John", "old", "new"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_throwsAuthenticationFailed_whenPasswordDoesNotMatch() {
        User user = generateUser();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(AuthenticationFailedException.class,
                () -> authenticationService.changePassword(user.getUsername(), "old", "new"));

        verify(userRepository, never()).save(any());
    }

}
