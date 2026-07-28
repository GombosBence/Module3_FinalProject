package com.example.springcore_module_3.service;

import com.example.springcore_module_3.exception.AuthenticationFailedException;
import com.example.springcore_module_3.exception.UnAuthorizedAccessException;
import com.example.springcore_module_3.model.User;
import com.example.springcore_module_3.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationServiceImpl(userRepository, passwordEncoder);
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
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPassword", user.getPassword())).thenReturn(Boolean.TRUE);

        assertDoesNotThrow(() -> authenticationService.authenticate("Test.User", "rawPassword"));
    }

    @Test
    void userNameDoesNotExistAuthenticationTest(){
        when(userRepository.findByUsername("Test.User")).thenReturn(Optional.empty());

        assertThrows(AuthenticationFailedException.class, () -> authenticationService.authenticate("Test.User", "rawPassword"));
    }

    @Test
    void passwordsDoNotMatchAuthenticationTest(){
        User user = generateUser();
        when(userRepository.findByUsername("Test.User")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPassword", user.getPassword())).thenReturn(false);

        assertThrows(AuthenticationFailedException.class, () -> authenticationService.authenticate("Test.User", "rawPassword"));
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
    void authenticateAndAuthorize_succeeds_whenActingOnOwnProfile() {
        User user = new User("John", "Doe", "John.Doe", "hashedPassword");
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPassword", "hashedPassword")).thenReturn(true);

        assertDoesNotThrow(() ->
                authenticationService.authenticateAndAuthorize("John.Doe", "rawPassword", "John.Doe"));
    }

    @Test
    void authenticateAndAuthorize_throwsUnauthorized_whenTargetUsernameDiffers() {
        User user = new User("John", "Doe", "John.Doe", "hashedPassword");
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPassword", "hashedPassword")).thenReturn(true);

        assertThrows(UnAuthorizedAccessException.class, () ->
                authenticationService.authenticateAndAuthorize("John.Doe", "rawPassword", "Someone.Else"));
    }

    @Test
    void authenticateAndAuthorize_throwsAuthenticationFailed_whenCredentialsInvalid_beforeCheckingOwnership() {
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.empty());

        assertThrows(AuthenticationFailedException.class, () ->
                authenticationService.authenticateAndAuthorize("John.Doe", "wrongPassword", "SomeoneElse.Entirely"));
    }

}
