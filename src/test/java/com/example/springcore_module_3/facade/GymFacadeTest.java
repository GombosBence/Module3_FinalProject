package com.example.springcore_module_3.facade;

import com.example.springcore_module_3.dto.AuthenticationRequestDto;
import com.example.springcore_module_3.dto.TraineeCreationResultDto;
import com.example.springcore_module_3.dto.TrainerCreationResultDto;
import com.example.springcore_module_3.model.*;
import com.example.springcore_module_3.service.TraineeService;
import com.example.springcore_module_3.service.TrainerService;
import com.example.springcore_module_3.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;

    private GymFacade facade;

    @BeforeEach
    void setUp() {
        facade = new GymFacade(traineeService, trainerService, trainingService);
    }

    @Test
    void createTrainee_delegatesToTraineeService_withCorrectArgsAndReturnValue() {
        User user = new User("John", "Doe", null, null);
        LocalDate dob = LocalDate.of(2000, 1, 1);
        TraineeCreationResultDto expected = new TraineeCreationResultDto(
                new Trainee(user, "123 Main St", dob), "rawPassword123");

        when(traineeService.createTraineeProfile(user, "123 Main St", dob)).thenReturn(expected);

        TraineeCreationResultDto result = facade.createTrainee(user, "123 Main St", dob);

        assertEquals(expected, result);
        verify(traineeService).createTraineeProfile(user, "123 Main St", dob);
    }

    @Test
    void deactivateTrainee_delegatesToTraineeService() {
        AuthenticationRequestDto credentials = new AuthenticationRequestDto("John.Doe", "rawPassword");

        facade.deactivateTrainee(credentials, 5L);

        verify(traineeService).deactivateTraineeProfile(credentials, 5L);
    }

    @Test
    void createTrainer_delegatesToTrainerService_withCorrectArgsAndReturnValue() {
        User user = new User("Mike", "Wilson", null, null);
        TrainingType fitness = new TrainingType("FITNESS");
        TrainerCreationResultDto expected = new TrainerCreationResultDto(
                new Trainer(user, fitness), "rawPassword456");

        when(trainerService.createTrainerProfile(user, fitness)).thenReturn(expected);

        TrainerCreationResultDto result = facade.createTrainer(user, fitness);

        assertEquals(expected, result);
        verify(trainerService).createTrainerProfile(user, fitness);
    }

    @Test
    void getTraineeTrainings_delegatesToTrainingService_andReturnsList() {
        AuthenticationRequestDto credentials = new AuthenticationRequestDto("John.Doe", "rawPassword");
        List<Training> expected = List.of(new Training(), new Training());
        LocalDate fromDate = LocalDate.of(2026, 1, 1);
        LocalDate toDate = LocalDate.of(2026, 12, 31);

        when(trainingService.selectTraineeTrainings(credentials, "John.Doe", fromDate, toDate, "Sara.Connor", null))
                .thenReturn(expected);

        List<Training> result = facade.getTraineeTrainings(credentials, "John.Doe", fromDate, toDate, "Sara.Connor", null);

        assertEquals(expected, result);
        verify(trainingService).selectTraineeTrainings(credentials, "John.Doe", fromDate, toDate, "Sara.Connor", null);
    }

    @Test
    void createTraining_delegatesToTrainingService_withCorrectArgsAndReturnValue() {
        AuthenticationRequestDto credentials = new AuthenticationRequestDto("John.Doe", "rawPassword");
        Training expected = new Training();
        LocalDate date = LocalDate.of(2026, 3, 14);
        Duration duration = Duration.ofMinutes(90);
        TrainingType fitness = new TrainingType("FITNESS");

        when(trainingService.createTraining(credentials, 1L, 3L, "Sample", fitness, date, duration))
                .thenReturn(expected);

        Training result = facade.createTraining(credentials, 1L, 3L, "Sample", fitness, date, duration);

        assertEquals(expected, result);
        verify(trainingService).createTraining(credentials, 1L, 3L, "Sample", fitness, date, duration);
    }

    @Test
    void updateTraineeTrainers_delegatesToTraineeService() {
        AuthenticationRequestDto credentials = new AuthenticationRequestDto("John.Doe", "rawPassword");
        List<Long> trainerIds = List.of(1L, 2L);

        facade.updateTraineeTrainers(credentials, "John.Doe", trainerIds);

        verify(traineeService).updateTraineeTrainers(credentials, "John.Doe", trainerIds);
    }
}