package com.example.springcore_module_3.service;

import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.Training;
import com.example.springcore_module_3.repository.TraineeRepository;
import com.example.springcore_module_3.repository.TrainerRepository;
import com.example.springcore_module_3.repository.TrainingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;

    private TrainingServiceImpl trainingService;

    @BeforeEach
    void setUp() {
        trainingService = new TrainingServiceImpl(trainingRepository, traineeRepository, trainerRepository);

        Authentication auth = new UsernamePasswordAuthenticationToken("John.Doe", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createTrainingSuccessTest() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername("Mike.Mentzer")).thenReturn(Optional.of(trainer));

        LocalDate date = LocalDate.of(2026, 3, 14);
        Duration duration = Duration.ofMinutes(90);

        Training training = assertDoesNotThrow(() -> trainingService.createTraining("John.Doe", "Mike.Mentzer",
                "Sample", date, duration));

        assertEquals("Sample", training.getTrainingName());
        assertEquals(date, training.getTrainingDate());
        assertEquals(duration, training.getTrainingDuration());
        verify(trainingRepository).save(training);
    }

    @Test
    void createTraining_traineeDoesNotExistTest() {
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainingService.createTraining("John.Doe", "Mike.Mentzer",
                "Sample", LocalDate.of(2026, 3, 14), Duration.ofMinutes(90)));

        verify(trainerRepository, never()).findByUserUsername(any());
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void createTraining_trainerDoesNotExistTest() {
        Trainee trainee = new Trainee();
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername("Mike.Mentzer")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainingService.createTraining("John.Doe", "Mike.Mentzer",
                "Sample", LocalDate.of(2026, 3, 14), Duration.ofMinutes(90)));

        verify(trainingRepository, never()).save(any());
    }

    @Test
    void selectTraineeTrainings_successfulTest() {
        when(trainingRepository.findTraineeTrainings(eq("John.Doe"), any(), any(), any(), any()))
                .thenReturn(List.of(new Training(), new Training()));

        List<Training> list = trainingService.selectTraineeTrainings("John.Doe", null, null, null, null);

        assertEquals(2, list.size());
        verify(trainingRepository).findTraineeTrainings(eq("John.Doe"), any(), any(), any(), any());
    }

    @Test
    void selectTrainerTrainings_successfulTest() {
        when(trainingRepository.findTrainerTrainings(eq("John.Doe"), any(), any(), any()))
                .thenReturn(List.of(new Training(), new Training()));

        List<Training> list = trainingService.selectTrainerTrainings("John.Doe", null, null, null);

        assertEquals(2, list.size());
        verify(trainingRepository).findTrainerTrainings(eq("John.Doe"), any(), any(), any());
    }
}