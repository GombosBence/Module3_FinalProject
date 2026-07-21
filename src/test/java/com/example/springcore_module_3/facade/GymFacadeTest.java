package com.example.springcore_module_3.facade;

import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.Training;
import com.example.springcore_module_3.model.TrainingType;
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
public class GymFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    private GymFacade gymFacade;

    @BeforeEach
    void setUp() {
        gymFacade = new GymFacade(traineeService, trainingService, trainerService);
    }

    @Test
    void createTrainee_delegatesToTraineeService() {
        Trainee trainee = new Trainee();
        trainee.setUsername("John.Doe");
        LocalDate dateOfBirth = LocalDate.of(2002, 12, 1);
        when(traineeService.createTraineeProfile("John", "Doe", "123 Main St.", dateOfBirth))
                .thenReturn(trainee);

        Trainee result = gymFacade.createTrainee("John", "Doe", "123 Main St.", dateOfBirth);
        assertEquals(trainee, result);
        verify(traineeService).createTraineeProfile("John", "Doe", "123 Main St.", dateOfBirth);
    }

    @Test
    void deleteTrainee_delegatesToTraineeService() {
        gymFacade.deleteTrainee(5L);

        verify(traineeService).deleteTraineeProfile(5L);
    }

    @Test
    void createTrainer_delegatesToTrainerService() {
        var expected = new com.example.springcore_module_3.model.Trainer();
        expected.setUsername("Mike.Wilson");
        when(trainerService.createTrainerProfile("Mike", "Wilson", TrainingType.FITNESS))
                .thenReturn(expected);

        var result = gymFacade.createTrainer("Mike", "Wilson", TrainingType.FITNESS);

        assertEquals(expected, result);
        verify(trainerService).createTrainerProfile("Mike", "Wilson", TrainingType.FITNESS);
    }

    @Test
    void getTrainingsByTrainee_delegatesToTrainingService_andReturnsList() {
        List<Training> expected = List.of(new Training(), new Training());
        when(trainingService.selectAllTrainingsByTrainee(1L)).thenReturn(expected);

        List<Training> result = gymFacade.getTrainingsByTrainee(1L);

        assertEquals(expected, result);
        verify(trainingService).selectAllTrainingsByTrainee(1L);
    }

    @Test
    void createTraining_delegatesToTrainingService_withCorrectArgsAndReturnValue() {
        Training expected = new Training();
        LocalDate date = LocalDate.of(2026, 3, 14);
        Duration duration = Duration.ofMinutes(90);
        when(trainingService.createTraining(1L, 3L, "Sample", TrainingType.FITNESS, date, duration))
                .thenReturn(expected);

        Training result = gymFacade.createTraining(1L, 3L, "Sample", TrainingType.FITNESS, date, duration);

        assertEquals(expected, result);
        verify(trainingService).createTraining(1L, 3L, "Sample", TrainingType.FITNESS, date, duration);
    }

}
