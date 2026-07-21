package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dao.TraineeDao;
import com.example.springcore_module_3.dao.TrainerDao;
import com.example.springcore_module_3.dao.TrainingDao;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceImplTest {

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    private TrainingServiceImpl trainingServiceImpl;

    @BeforeEach
    public void setUp() {
        trainingServiceImpl = new TrainingServiceImpl();
        trainingServiceImpl.setTrainingDao(trainingDao);
        trainingServiceImpl.setTraineeDao(traineeDao);
        trainingServiceImpl.setTrainerDao(trainerDao);
    }

    private Training sampleTraining(Long traineeId, Long trainerId){
        Training sampleTraining = new Training();
        sampleTraining.setTraineeId(traineeId);
        sampleTraining.setTrainerId(trainerId);
        return sampleTraining;
    }

    @Test
    void createTrainingSuccessTest(){
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        trainee.setUserId(1L);
        trainer.setUserId(3L);
        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerDao.findById(3L)).thenReturn(Optional.of(trainer));

        Training training = assertDoesNotThrow(() -> trainingServiceImpl.createTraining(1L, 3L, "Sample",
                TrainingType.FITNESS, LocalDate.of(2026, 3, 14), Duration.ofMinutes(90)));

        verify(trainingDao).create(training);
    }

    @Test
    void createTraining_traineeDoesNotExistTest(){
        Trainee trainee = new Trainee();
        trainee.setUserId(1L);

        when(traineeDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainingServiceImpl.createTraining(1L, 3L, "Sample",
                TrainingType.FITNESS, LocalDate.of(2026, 3, 14), Duration.ofMinutes(90)));
    }

    @Test
    void createTraining_trainerDoesNotExistTest(){
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        trainee.setUserId(1L);
        trainer.setUserId(3L);
        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerDao.findById(3L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainingServiceImpl.createTraining(1L, 3L, "Sample",
                TrainingType.FITNESS, LocalDate.of(2026, 3, 14), Duration.ofMinutes(90)));

    }

    @Test
    void findTrainingByIdSuccessTest(){
        Training training = new Training();
        training.setTrainingId(1L);
        when(trainingDao.findById(1L)).thenReturn(Optional.of(training));

        assertDoesNotThrow(() -> trainingServiceImpl.getTrainingById(1L));
        verify(trainingDao).findById(1L);
    }

    @Test
    void findTrainingByIdFailureTest(){
        Training training = new Training();
        training.setTrainingId(999L);
        when(trainingDao.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainingServiceImpl.getTrainingById(999L));
    }

    @Test
    void findTrainingsByTraineeSuccessTest(){
        List<Training> trainingsList = List.of(
                sampleTraining(1L, 3L),
                sampleTraining(1L, 1L)
        );
        when(trainingDao.findAllByTrainee(1L)).thenReturn(trainingsList);

        List<Training> result = trainingServiceImpl.selectAllTrainingsByTrainee(1L);
        assertEquals(trainingsList.size(), result.size());
    }

    @Test
    void findTrainingsByTraineeEmptyTest(){
        List<Training> trainingsList = List.of();
        when(trainingDao.findAllByTrainee(1L)).thenReturn(trainingsList);

        List<Training> result = trainingServiceImpl.selectAllTrainingsByTrainee(1L);
        assertTrue(result.isEmpty());
    }

    @Test
    void findTrainingsByTrainerSuccessTest(){
        List<Training> trainingsList = List.of(
                sampleTraining(2L, 3L),
                sampleTraining(1L, 3L)
        );
        when(trainingDao.findAllByTrainer(3L)).thenReturn(trainingsList);

        List<Training> result = trainingServiceImpl.selectAllTrainingsByTrainer(3L);
        assertEquals(trainingsList.size(), result.size());
    }

    @Test
    void findTrainingsByTrainerEmptyTest(){
        List<Training> trainingsList = List.of();
        when(trainingDao.findAllByTrainer(3L)).thenReturn(trainingsList);

        List<Training> result = trainingServiceImpl.selectAllTrainingsByTrainer(3L);
        assertTrue(result.isEmpty());
    }
}

