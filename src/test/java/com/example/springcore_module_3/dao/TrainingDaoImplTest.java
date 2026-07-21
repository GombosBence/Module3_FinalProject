package com.example.springcore_module_3.dao;

import com.example.springcore_module_3.model.Training;
import com.example.springcore_module_3.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TrainingDaoImplTest {

    private Map<Long, Training> storage;
    private TrainingDaoImpl dao;

    @BeforeEach
    public void setUp() {
        storage = new HashMap<>();
        dao = new TrainingDaoImpl();
        dao.setStorage(storage);
        dao.init();
    }

    private Training sampleTraining(Long traineeId, Long trainerId, String trainingName){
        Training sampleTraining = new Training();
        sampleTraining.setTraineeId(traineeId);
        sampleTraining.setTrainerId(trainerId);
        sampleTraining.setTrainingName(trainingName);
        sampleTraining.setTrainingType(TrainingType.FITNESS);
        sampleTraining.setTrainingDate(LocalDate.of(2026, 7, 24));
        sampleTraining.setTrainingDuration(Duration.ofMinutes(90));
        return sampleTraining;
    }

    @Test
    void createTrainingTest(){
        Training sampleTraining = sampleTraining(1L, 1L, "Sample Training");

        Training created =  dao.create(sampleTraining);

        assertTrue(storage.containsKey(1L));
        assertEquals(1L, created.getTrainingId());
        assertEquals(sampleTraining, storage.get(1L));
    }

    @Test
    void getTrainingByIdSuccessTest(){
        Training sampleTraining = dao.create(sampleTraining(1L, 1L, "Sample Training"));

        Optional<Training> found = dao.findById(sampleTraining.getTrainingId());

        assertTrue(found.isPresent());
    }

    @Test
    void getTrainingByIdFailureTest(){
        Training sampleTraining = sampleTraining(1L, 1L, "Sample Training");
        sampleTraining.setTrainingId(999L);

        Optional<Training> found = dao.findById(sampleTraining.getTrainingId());

        assertTrue(found.isEmpty());
    }

    @Test
    void findAllByTraineeTest(){

        Training training1 = dao.create(sampleTraining(1L, 1L, "Sample Training"));
        Training training2 = dao.create(sampleTraining(1L, 3L, "Other training"));
        Training training3 = dao.create(sampleTraining(2L, 1L, "Third"));

        List<Training> trainings = dao.findAllByTrainee(1L);

        assertEquals(2, trainings.size());
        assertTrue(trainings.contains(training1));
        assertTrue(trainings.contains(training2));
        assertFalse(trainings.contains(training3));
    }

    @Test
    void findAllByTrainee_emptyListTest(){
        dao.create(sampleTraining(1L, 1L, "Sample Training"));
        dao.create(sampleTraining(1L, 3L, "Other training"));
        dao.create(sampleTraining(2L, 1L, "Third"));

        List<Training> trainings = dao.findAllByTrainee(3L);

        assertTrue(trainings.isEmpty());
    }

    @Test
    void findAllByTrainerTest(){
        Training training1 = dao.create(sampleTraining(1L, 1L, "Sample Training"));
        Training training2 = dao.create(sampleTraining(1L, 3L, "Other training"));
        Training training3 = dao.create(sampleTraining(2L, 1L, "Third"));

        List<Training> trainings = dao.findAllByTrainer(3L);

        assertEquals(1, trainings.size());
        assertFalse(trainings.contains(training1));
        assertTrue(trainings.contains(training2));
        assertFalse(trainings.contains(training3));
    }

    @Test
    void findAllByTrainer_emptyListTest(){
        dao.create(sampleTraining(1L, 1L, "Sample Training"));
        dao.create(sampleTraining(1L, 3L, "Other training"));
        dao.create(sampleTraining(2L, 1L, "Third"));

        List<Training> trainings = dao.findAllByTrainer(4L);

        assertTrue(trainings.isEmpty());
    }
}
