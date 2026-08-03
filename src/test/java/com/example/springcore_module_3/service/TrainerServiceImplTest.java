package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dto.request.AuthenticationRequest;
import com.example.springcore_module_3.dto.TrainerCreationResult;
import com.example.springcore_module_3.exception.AuthenticationFailedException;
import com.example.springcore_module_3.exception.InvalidStateTransitionException;
import com.example.springcore_module_3.exception.UnAuthorizedAccessException;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.TrainingType;
import com.example.springcore_module_3.model.User;
import com.example.springcore_module_3.repository.TrainerRepository;
import com.example.springcore_module_3.repository.UserRepository;
import com.example.springcore_module_3.util.PasswordGenerator;
import com.example.springcore_module_3.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private UsernameGenerator usernameGenerator;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationService authenticationService;

    private TrainerServiceImpl trainerService;

    @BeforeEach
    void setUp() {
        trainerService = new TrainerServiceImpl(trainerRepository, passwordGenerator, usernameGenerator,
                passwordEncoder, userRepository, authenticationService);
    }

    private TrainingType fitness() {
        TrainingType type = new TrainingType("FITNESS");
        type.setTrainingTypeId(1L);
        return type;
    }

    @Test
    void createTrainerProfileTest() {
        when(usernameGenerator.generateUsername(eq("Mike"), eq("Wilson"), any()))
                .thenReturn("Mike.Wilson");
        when(passwordGenerator.generatePassword(10)).thenReturn("QWERTY12345");

        User user = new User("Mike", "Wilson", "Mike.Wilson", "QWERTY12345");
        TrainerCreationResult result = trainerService.createTrainerProfile(user, fitness());

        assertEquals("Mike.Wilson", result.trainer().getUser().getUsername());
        assertEquals("FITNESS", result.trainer().getSpecialization().getTrainingTypeName());
        verify(passwordGenerator).generatePassword(10);
        verify(passwordEncoder).encode("QWERTY12345");
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void updateTrainerProfileSuccessTest() {
        User user = new User("Mike", "Wilson", "Mike.Wilson", "hashedPw");
        Trainer old = new Trainer(user, fitness());
        Trainer updated = new Trainer(user, fitness());
        AuthenticationRequest credentials = new AuthenticationRequest("Mike.Wilson", "password");

        when(trainerRepository.findByUserUsername("Mike.Wilson")).thenReturn(Optional.of(old));

        assertDoesNotThrow(() -> trainerService.updateTrainerProfile(credentials, updated));
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void updateTrainerProfile_throws_whenAuthenticationFails() {
        User user = new User("Mike", "Wilson", "Mike.Wilson", "hashedPw");
        Trainer incoming = new Trainer(user, fitness());
        AuthenticationRequest credentials = new AuthenticationRequest("Mike.Wilson", "wrongPassword");

        when(trainerRepository.findByUserUsername("Mike.Wilson")).thenReturn(Optional.of(incoming));
        doThrow(new AuthenticationFailedException("Invalid username or password"))
                .when(authenticationService).authenticateAndAuthorize("Mike.Wilson", "wrongPassword", "Mike.Wilson");

        assertThrows(AuthenticationFailedException.class,
                () -> trainerService.updateTrainerProfile(credentials, incoming));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    void updateTrainerProfile_throwsUnauthorized_whenActingOnDifferentProfile() {
        User targetUser = new User("Mike", "Wilson", "Mike.Wilson", "hashedPw");
        Trainer targetTrainer = new Trainer(targetUser, fitness());
        AuthenticationRequest credentials = new AuthenticationRequest("Eve.Evil", "password");

        when(trainerRepository.findByUserUsername("Mike.Wilson")).thenReturn(Optional.of(targetTrainer));
        doThrow(new UnAuthorizedAccessException("You are not authorized to perform this operation"))
                .when(authenticationService).authenticateAndAuthorize("Eve.Evil", "password", "Mike.Wilson");

        assertThrows(UnAuthorizedAccessException.class,
                () -> trainerService.updateTrainerProfile(credentials, targetTrainer));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    void selectTrainerProfileByIdSuccessTest() {
        User user = new User("Mike", "Wilson", "Mike.Wilson", "hashedPw");
        Trainer trainer = new Trainer(user, fitness());
        trainer.setTrainerId(1L);
        AuthenticationRequest credentials = new AuthenticationRequest("Mike.Wilson", "password");

        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.selectTrainerProfile(credentials, 1L);

        assertEquals("Mike.Wilson", result.getUser().getUsername());
        verify(authenticationService).authenticate("Mike.Wilson", "password");
    }

    @Test
    void selectTrainerProfileByIdFailureTest() {
        AuthenticationRequest credentials = new AuthenticationRequest("Mike.Wilson", "password");
        when(trainerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> trainerService.selectTrainerProfile(credentials, 999L));
    }

    @Test
    void selectTrainerProfileByUsernameSuccessTest() {
        User user = new User("Mike", "Wilson", "Mike.Wilson", "hashedPw");
        Trainer trainer = new Trainer(user, fitness());
        AuthenticationRequest credentials = new AuthenticationRequest("Mike.Wilson", "password");

        when(trainerRepository.findByUserUsername("Mike.Wilson")).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.selectTrainerProfileByUsername(credentials, "Mike.Wilson");

        assertEquals("Mike.Wilson", result.getUser().getUsername());
        verify(authenticationService).authenticate("Mike.Wilson", "password");
    }

    @Test
    void deactivateTrainerProfile_setsInactive_whenTrainerExists() {
        User user = new User("Mike", "Wilson", "Mike.Wilson", "hashedPw");
        Trainer trainer = new Trainer(user, fitness());
        trainer.setTrainerId(1L);
        trainer.getUser().setActive(true);
        AuthenticationRequest credentials = new AuthenticationRequest("Mike.Wilson", "password");

        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        assertDoesNotThrow(() -> trainerService.deactivateTrainerProfile(credentials, 1L));

        assertFalse(trainer.getUser().isActive());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void deactivateTrainerProfile_throws_whenTrainerDoesNotExist() {
        AuthenticationRequest credentials = new AuthenticationRequest("Mike.Wilson", "password");
        when(trainerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> trainerService.deactivateTrainerProfile(credentials, 999L));
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void deactivateTrainerProfile_throws_whenAlreadyInactive() {
        User user = new User("Mike", "Wilson", "Mike.Wilson", "hashedPw");
        Trainer trainer = new Trainer(user, fitness());
        trainer.setTrainerId(1L);
        trainer.getUser().setActive(false);
        AuthenticationRequest credentials = new AuthenticationRequest("Mike.Wilson", "password");

        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        assertThrows(InvalidStateTransitionException.class,
                () -> trainerService.deactivateTrainerProfile(credentials, 1L));
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void activateTrainerProfile_setsActive_whenTrainerExists() {
        User user = new User("Mike", "Wilson", "Mike.Wilson", "hashedPw");
        Trainer trainer = new Trainer(user, fitness());
        trainer.setTrainerId(1L);
        trainer.getUser().setActive(false);
        AuthenticationRequest credentials = new AuthenticationRequest("Mike.Wilson", "password");

        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        assertDoesNotThrow(() -> trainerService.activateTrainerProfile(credentials, 1L));

        assertTrue(trainer.getUser().isActive());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void activateTrainerProfile_throws_whenAlreadyActive() {
        User user = new User("Mike", "Wilson", "Mike.Wilson", "hashedPw");
        Trainer trainer = new Trainer(user, fitness());
        trainer.setTrainerId(1L);
        trainer.getUser().setActive(true);
        AuthenticationRequest credentials = new AuthenticationRequest("Mike.Wilson", "password");

        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        assertThrows(InvalidStateTransitionException.class,
                () -> trainerService.activateTrainerProfile(credentials, 1L));
        verify(trainerRepository, never()).save(any());
    }
}