package com.example.springcore_module_3.facade;

import com.example.springcore_module_3.dto.request.AuthenticationRequest;
import com.example.springcore_module_3.dto.TraineeCreationResult;
import com.example.springcore_module_3.dto.TrainerCreationResult;
import com.example.springcore_module_3.model.*;
import com.example.springcore_module_3.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private TrainingTypeService trainingTypeService;

    private GymFacade facade;

    @BeforeEach
    void setUp() {
        facade = new GymFacade(traineeService, trainerService, trainingService, authenticationService, trainingTypeService);
    }

    @Test
    void createTrainee_delegatesToTraineeService_withCorrectArgsAndReturnValue() {
        User user = new User("John", "Doe", null, null);
        LocalDate dob = LocalDate.of(2000, 1, 1);
        TraineeCreationResult expected = new TraineeCreationResult(
                new Trainee(user, "123 Main St", dob), "rawPassword123");

        when(traineeService.createTraineeProfile(any(User.class), eq("123 Main St"), eq(dob))).thenReturn(expected);

        TraineeCreationResult result = facade.createTrainee("John", "Doe", "123 Main St", dob);

        assertEquals(expected, result);
        verify(traineeService).createTraineeProfile(any(User.class), eq("123 Main St"), eq(dob));
    }

    @Test
    void deactivateTrainee_delegatesToTraineeService() {
        facade.deactivateTrainee( "John.Doe");
        verify(traineeService).deactivateTraineeProfile("John.Doe");
    }

    @Test
    void createTrainer_delegatesToTrainerService_withCorrectArgsAndReturnValue() {
        User user = new User("Mike", "Wilson", null, null);
        TrainingType fitness = new TrainingType("FITNESS");
        TrainerCreationResult expected = new TrainerCreationResult(
                new Trainer(user, fitness), "rawPassword456");

        when(trainerService.createTrainerProfile(any(User.class), any(TrainingType.class))).thenReturn(expected);

        TrainerCreationResult result = facade.createTrainer(user.getFirstName(), user.getLastName(), fitness);

        assertEquals(expected, result);
        verify(trainerService).createTrainerProfile(any(User.class), any(TrainingType.class));
    }

    @Test
    void getTraineeTrainings_delegatesToTrainingService_andReturnsList() {
        List<Training> expected = List.of(new Training(), new Training());
        LocalDate fromDate = LocalDate.of(2026, 1, 1);
        LocalDate toDate = LocalDate.of(2026, 12, 31);

        when(trainingService.selectTraineeTrainings( "John.Doe", fromDate, toDate, "Sara.Connor", null))
                .thenReturn(expected);

        List<Training> result = facade.getTraineeTrainings("John.Doe", fromDate, toDate, "Sara.Connor", null);

        assertEquals(expected, result);
        verify(trainingService).selectTraineeTrainings(eq("John.Doe"),
                eq(fromDate), eq(toDate), eq("Sara.Connor"), any());
    }

    @Test
    void createTraining_delegatesToTrainingService_withCorrectArgsAndReturnValue() {
        Training expected = new Training();
        LocalDate date = LocalDate.of(2026, 3, 14);
        Duration duration = Duration.ofMinutes(90);

        when(trainingService.createTraining("John.Doe", "Mike.Mentzer", "Sample", date, duration))
                .thenReturn(expected);
        facade.createTraining( "John.Doe", "Mike.Mentzer", "Sample", date, duration);

        verify(trainingService).createTraining("John.Doe", "Mike.Mentzer", "Sample", date, duration);
    }

    @Test
    void updateTraineeTrainers_delegatesToTraineeService() {
        List<String> trainerUsernames = List.of("Mike.Mentzer", "Jane.Roe");

        facade.updateTraineeTrainers( "John.Doe", trainerUsernames);
        verify(traineeService).updateTraineeTrainers("John.Doe", trainerUsernames );
    }
}