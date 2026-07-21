package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dao.TraineeDao;
import com.example.springcore_module_3.dao.TrainerDao;
import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.util.PasswordGenerator;
import com.example.springcore_module_3.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TraineeServiceImplTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;


    private TraineeServiceImpl traineeService;

    @BeforeEach
    void setUp() {
        traineeService = new TraineeServiceImpl();
        traineeService.setTraineeDao(traineeDao);
        traineeService.setTrainerDao(trainerDao);
        traineeService.setUsernameGenerator(usernameGenerator);
        traineeService.setPasswordGenerator(passwordGenerator);
    }


    @Test
    void createTraineeProfileTest() {
        when(usernameGenerator.generateUsername(eq("John"), eq("Doe"), any()))
        .thenReturn("John.Doe");
        when(passwordGenerator.generatePassword(10)).thenReturn("ABCDE12345");

        Trainee result = traineeService.createTraineeProfile("John", "Doe", "Budapest main street 1",
                LocalDate.of(1989, 4,11));

        assertEquals("John.Doe", result.getUsername());
        assertEquals("ABCDE12345", result.getPassword());
        verify(traineeDao).create(result);
    }

    @Test
    void updateTraineeProfileSuccessTest() {
        Trainee trainee = new Trainee();
        trainee.setUserId(1L);
        when(traineeDao.update(trainee)).thenReturn(true);

        assertDoesNotThrow(() -> traineeService.updateTraineeProfile(trainee));
        verify(traineeDao).update(trainee);
    }

    @Test
    void updateTraineeProfileFailureTest() {
        Trainee trainee = new Trainee();
        trainee.setUserId(999L);
        when(traineeDao.update(trainee)).thenReturn(false);

        assertThrows(
                NoSuchElementException.class
                ,() -> traineeService.updateTraineeProfile(trainee));
    }

    @Test
    void deleteTraineeProfileSuccessTest() {
        Trainee trainee = new Trainee();
        trainee.setUserId(1L);
        when(traineeDao.delete(1L)).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.deleteTraineeProfile(1L));
        verify(traineeDao).delete(1L);

    }

    @Test
    void deleteTraineeProfileFailureTest() {
        Trainee trainee = new Trainee();
        trainee.setUserId(999L);
        when(traineeDao.delete(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> traineeService.deleteTraineeProfile(999L));
    }

    @Test
    void selectTraineeProfileByIdSuccessTest() {
        Trainee trainee = new Trainee();
        trainee.setUserId(1L);
        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.selectTraineeProfile(1L));
        verify(traineeDao).findById(1L);
    }

    @Test
    void selectTraineeProfileByIdFailureTest() {
        Trainee trainee = new Trainee();
        trainee.setUserId(999L);
        when(traineeDao.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> traineeService.selectTraineeProfile(999L));
    }

    @Test
    void selectTraineeProfileByUsernameSuccessTest() {
        Trainee trainee = new Trainee();
        trainee.setUsername("John.Doe");
        when(traineeDao.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.selectTraineeProfileByUsername("John.Doe"));
        verify(traineeDao).findByUsername("John.Doe");
    }

    @Test
    void selectTraineeProfileByUsernameFailureTest() {
        Trainee trainee = new Trainee();
        trainee.setUsername("John.Doe");
        when(traineeDao.findByUsername("John.Doe")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> traineeService.selectTraineeProfileByUsername("John.Doe"));
    }

}
