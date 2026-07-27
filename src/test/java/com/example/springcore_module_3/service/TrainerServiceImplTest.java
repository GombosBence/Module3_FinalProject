package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dao.TraineeDao;
import com.example.springcore_module_3.dao.TrainerDao;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.TrainingType;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceImplTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private PasswordGenerator passwordGenerator;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsernameRegistry usernameRegistry;

    private TrainerServiceImpl trainerService;

    @BeforeEach
    void setUp() {
        trainerService = new TrainerServiceImpl();
        trainerService.setTraineeDao(traineeDao);
        trainerService.setTrainerDao(trainerDao);
        trainerService.setPasswordGenerator(passwordGenerator);
        trainerService.setUsernameGenerator(usernameGenerator);
        trainerService.setPasswordEncoder(passwordEncoder);
        trainerService.setUsernameRegistry(usernameRegistry);
    }

    @Test
    void createTrainerProfileTest(){
        when(usernameGenerator.generateUsername(eq("John"), eq("Doe"), any())).thenReturn("John.Doe");
        when(passwordGenerator.generatePassword(10)).thenReturn("ABCDE12345");

        Trainer trainer = trainerService.createTrainerProfile(, "John", TrainingType.FITNESS);

        assertEquals("John.Doe", trainer.getUsername());
        assertEquals(TrainingType.FITNESS, trainer.getSpecialization());
        verify(passwordGenerator).generatePassword(10);
        verify(passwordEncoder).encode("ABCDE12345");
        verify(usernameGenerator).generateUsername(eq("John"), eq("Doe"), any());
        verify(trainerDao).create(trainer);
    }

    @Test
    void updateTrainerProfileSuccessTest(){
        Trainer trainer = new Trainer();
        trainer.setUserId(1L);
        when(trainerDao.update(trainer)).thenReturn(true);

        assertDoesNotThrow(() -> trainerService.updateTrainerProfile(, trainer));
        verify(trainerDao).update(trainer);
    }

    @Test
    void updateTrainerProfileFailureTest(){
        Trainer trainer = new Trainer();
        trainer.setUserId(999L);
        when(trainerDao.update(trainer)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> trainerService.updateTrainerProfile(, trainer));
    }

    @Test
    void selectTrainerProfileByIdSuccessTest(){
        Trainer trainer = new Trainer();
        trainer.setUserId(1L);
        when(trainerDao.findById(1L)).thenReturn(Optional.of(trainer));

        Trainer result = assertDoesNotThrow(() -> trainerService.selectTrainerProfile(, 1L));
        assertEquals(1L, result.getUserId());
        verify(trainerDao).findById(1L);
    }

    @Test
    void selectTrainerProfileByIdFailureTest(){
        Trainer trainer = new Trainer();
        trainer.setUserId(999L);
        when(trainerDao.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainerService.selectTrainerProfile(, 999L));
    }

    @Test
    void selectTrainerProfileByUsernameSuccessTest(){
        Trainer trainer = new Trainer();
        trainer.setUsername("John.Doe");
        when(trainerDao.findByUsername("John.Doe")).thenReturn(Optional.of(trainer));

        assertDoesNotThrow(() -> trainerService.selectTrainerProfileByUsername(, "John.Doe"));
        verify(trainerDao).findByUsername("John.Doe");
    }

    @Test
    void selectTrainerProfileByUsernameFailureTest(){
        Trainer trainer = new Trainer();
        trainer.setUsername("John.Doe");
        when(trainerDao.findByUsername("John.Doe")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainerService.selectTrainerProfileByUsername(, "John.Doe"));
    }

}
