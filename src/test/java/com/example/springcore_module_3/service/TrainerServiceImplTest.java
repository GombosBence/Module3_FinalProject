package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dto.TrainerCreationResult;
import com.example.springcore_module_3.exception.InvalidStateTransitionException;
import com.example.springcore_module_3.exception.UnAuthorizedAccessException;
import com.example.springcore_module_3.metrics.GymMetrics;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.TrainingType;
import com.example.springcore_module_3.model.User;
import com.example.springcore_module_3.repository.TrainerRepository;
import com.example.springcore_module_3.repository.UserRepository;
import com.example.springcore_module_3.util.AuthorizationHelper;
import com.example.springcore_module_3.util.PasswordGenerator;
import com.example.springcore_module_3.util.UsernameGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
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
    private AuthorizationHelper authorizationHelper;
    @Mock
    private GymMetrics gymMetrics;

    private TrainerServiceImpl trainerService;

    @BeforeEach
    void setUp() {
        trainerService = new TrainerServiceImpl(trainerRepository, passwordGenerator, usernameGenerator,
                passwordEncoder, userRepository, authorizationHelper, gymMetrics);

        Authentication auth = new UsernamePasswordAuthenticationToken("Mike.Wilson", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
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

        when(trainerRepository.findByUserUsername("Mike.Wilson")).thenReturn(Optional.of(old));

        assertDoesNotThrow(() -> trainerService.updateTrainerProfile(updated));

        verify(authorizationHelper).requireOwnAccount("Mike.Wilson");
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void updateTrainerProfile_throwsUnauthorized_whenActingOnDifferentProfile() {
        User targetUser = new User("Mike", "Wilson", "Mike.Wilson", "hashedPw");
        Trainer targetTrainer = new Trainer(targetUser, fitness());

        when(trainerRepository.findByUserUsername("Mike.Wilson")).thenReturn(Optional.of(targetTrainer));
        doThrow(new UnAuthorizedAccessException("You are not authorized to perform this operation"))
                .when(authorizationHelper).requireOwnAccount("Mike.Wilson");

        assertThrows(UnAuthorizedAccessException.class,
                () -> trainerService.updateTrainerProfile(targetTrainer));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    void selectTrainerProfileByIdSuccessTest() {
        User user = new User("Mike", "Wilson", "Mike.Wilson", "hashedPw");
        Trainer trainer = new Trainer(user, fitness());
        trainer.setTrainerId(1L);

        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.selectTrainerProfile(1L);

        assertEquals("Mike.Wilson", result.getUser().getUsername());
    }

    @Test
    void selectTrainerProfileByIdFailureTest() {
        when(trainerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> trainerService.selectTrainerProfile(999L));
    }

    @Test
    void selectTrainerProfileByUsernameSuccessTest() {
        User user = new User("Mike", "Wilson", "Mike.Wilson", "hashedPw");
        Trainer trainer = new Trainer(user, fitness());

        when(trainerRepository.findByUserUsername("Mike.Wilson")).thenReturn(Optional.of(trainer));
        Trainer result = trainerService.selectTrainerProfileByUsername("Mike.Wilson");

        assertEquals("Mike.Wilson", result.getUser().getUsername());
    }

    @Test
    void selectTrainerProfileByUsernameFailureTest() {
        when(trainerRepository.findByUserUsername("Nobody.Here")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> trainerService.selectTrainerProfileByUsername("Nobody.Here"));
    }

    @Test
    void deactivateTrainerProfile_setsInactive_whenTrainerExists() {
        User user = new User("Mike", "Wilson", "Mike.Wilson", "hashedPw");
        Trainer trainer = new Trainer(user, fitness());
        trainer.setTrainerId(1L);
        trainer.getUser().setActive(true);

        when(trainerRepository.findByUserUsername(user.getUsername())).thenReturn(Optional.of(trainer));

        assertDoesNotThrow(() -> trainerService.deactivateTrainerProfile(user.getUsername()));

        assertFalse(trainer.getUser().isActive());
        verify(authorizationHelper).requireOwnAccount("Mike.Wilson");
        verify(trainerRepository).save(trainer);
    }

    @Test
    void deactivateTrainerProfile_throws_whenTrainerDoesNotExist() {
        when(trainerRepository.findByUserUsername("Mike.Wilson")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> trainerService.deactivateTrainerProfile("Mike.Wilson"));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    void deactivateTrainerProfile_throws_whenAlreadyInactive() {
        User user = new User("Mike", "Wilson", "Mike.Wilson", "hashedPw");
        Trainer trainer = new Trainer(user, fitness());
        trainer.setTrainerId(1L);
        trainer.getUser().setActive(false);

        when(trainerRepository.findByUserUsername(user.getUsername())).thenReturn(Optional.of(trainer));

        assertThrows(InvalidStateTransitionException.class,
                () -> trainerService.deactivateTrainerProfile(user.getUsername()));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    void activateTrainerProfile_setsActive_whenTrainerExists() {
        User user = new User("Mike", "Wilson", "Mike.Wilson", "hashedPw");
        Trainer trainer = new Trainer(user, fitness());
        trainer.setTrainerId(1L);
        trainer.getUser().setActive(false);

        when(trainerRepository.findByUserUsername(user.getUsername())).thenReturn(Optional.of(trainer));

        assertDoesNotThrow(() -> trainerService.activateTrainerProfile(user.getUsername()));

        assertTrue(trainer.getUser().isActive());
        verify(authorizationHelper).requireOwnAccount("Mike.Wilson");
        verify(trainerRepository).save(trainer);
    }

    @Test
    void activateTrainerProfile_throws_whenTrainerDoesNotExist() {
        when(trainerRepository.findByUserUsername("Mike.Wilson")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> trainerService.activateTrainerProfile("Mike.Wilson"));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    void activateTrainerProfile_throws_whenAlreadyActive() {
        User user = new User("Mike", "Wilson", "Mike.Wilson", "hashedPw");
        Trainer trainer = new Trainer(user, fitness());
        trainer.setTrainerId(1L);
        trainer.getUser().setActive(true);

        when(trainerRepository.findByUserUsername(user.getUsername())).thenReturn(Optional.of(trainer));

        assertThrows(InvalidStateTransitionException.class,
                () -> trainerService.activateTrainerProfile(user.getUsername()));

        verify(trainerRepository, never()).save(any());
    }
}