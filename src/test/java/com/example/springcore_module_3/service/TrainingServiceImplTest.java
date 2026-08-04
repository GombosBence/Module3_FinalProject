package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dto.request.AuthenticationRequest;
import com.example.springcore_module_3.exception.AuthenticationFailedException;
import com.example.springcore_module_3.repository.*;
import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.Training;
import com.example.springcore_module_3.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private AuthenticationService authenticationService;

    private TrainingService trainingService;

    @BeforeEach
    public void setUp() {
        trainingService = new TrainingServiceImpl(trainingRepository,traineeRepository, trainerRepository, authenticationService);
    }

    private TrainingType fitness() {
        TrainingType type = new TrainingType("FITNESS");
        type.setTrainingTypeId(1L);
        return type;
    }

    @Test
    void createTrainingSuccessTest(){
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        TrainingType type = fitness();
        when(traineeRepository.findByUserUsername(any())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername(any())).thenReturn(Optional.of(trainer));
        LocalDate date = LocalDate.of(2026, 3, 14);
        Duration duration = Duration.ofMinutes(90);
        AuthenticationRequest credentials = new AuthenticationRequest("John.Doe", "rawPw");

        Training training = assertDoesNotThrow(() -> trainingService.createTraining(credentials, "John.Doe", "Mike.Mentzer",
                "Sample", date, duration));

        assertEquals("Sample", training.getTrainingName());
        assertEquals(date, training.getTrainingDate());
        assertEquals(duration, training.getTrainingDuration());
        verify(authenticationService).authenticate(credentials.username(), credentials.password());
        verify(trainingRepository).save(training);
    }

    @Test
    void createTraining_traineeDoesNotExistTest(){
        AuthenticationRequest credentials = new AuthenticationRequest("John.Doe", "rawPw");

        assertThrows(NoSuchElementException.class, () -> trainingService.createTraining(credentials, "John.Doe", "Mike.Mentzer",
                "Sample", LocalDate.of(2026, 3, 14), Duration.ofMinutes(90)));
    }

    @Test
    void createTraining_trainerDoesNotExistTest(){
        Trainee trainee = new Trainee();
        trainee.setTraineeId(1L);
        AuthenticationRequest credentials = new AuthenticationRequest("John.Doe", "rawPw");

        assertThrows(NoSuchElementException.class, () -> trainingService.createTraining(credentials, "John.Doe", "Mike.Mentzer",
                "Sample", LocalDate.of(2026, 3, 14), Duration.ofMinutes(90)));

    }

    @Test
    void selectTraineeTrainings_successfulTest(){
        when(trainingRepository.findTraineeTrainings(any(),any(), any(),any(), any())).thenReturn(List.of(
                new Training(),
                new Training()
        ));
        AuthenticationRequest credentials = new AuthenticationRequest("John.Doe", "rawPw");

        List<Training> list = trainingService.selectTraineeTrainings(credentials, "John.Doe", null, null, null, null);

        assertFalse(list.isEmpty());
        verify(trainingRepository).findTraineeTrainings(any(),any(), any(),any(), any());
        verify(authenticationService).authenticate(credentials.username(), credentials.password());
    }

    @Test
    void selectTrainee_AuthenticationFailedTest(){

        AuthenticationRequest credentials = new AuthenticationRequest("John.Doe", "rawPw");
        doThrow(AuthenticationFailedException.class).when(authenticationService).authenticate("John.Doe", "rawPw");

        assertThrows(AuthenticationFailedException.class,
                () -> trainingService.selectTraineeTrainings(credentials, "John.Doe", null, null, null, null));
        verify(authenticationService).authenticate("John.Doe", "rawPw");
        verify(trainingRepository, never()).findTraineeTrainings(any(), any(), any(), any(), any());
    }

    @Test
    void selectTrainerTrainings_successfulTest(){
        when(trainingRepository.findTrainerTrainings(any(),any(), any(),any())).thenReturn(List.of(
                new Training(),
                new Training()
        ));
        AuthenticationRequest credentials = new AuthenticationRequest("John.Doe", "rawPw");

        List<Training> list = trainingService.selectTrainerTrainings(credentials, "John.Doe", null, null, null);

        assertFalse(list.isEmpty());
        verify(trainingRepository).findTrainerTrainings(any(),any(), any(),any());
        verify(authenticationService).authenticate(credentials.username(), credentials.password());
    }

    @Test
    void selectTrainer_AuthenticationFailedTest(){

        AuthenticationRequest credentials = new AuthenticationRequest("John.Doe", "rawPw");
        doThrow(AuthenticationFailedException.class).when(authenticationService).authenticate("John.Doe", "rawPw");

        assertThrows(AuthenticationFailedException.class,
                () -> trainingService.selectTrainerTrainings(credentials, "John.Doe", null, null, null));
        verify(authenticationService).authenticate("John.Doe", "rawPw");
        verify(trainingRepository, never()).findTrainerTrainings(any(), any(), any(), any());
    }
}

