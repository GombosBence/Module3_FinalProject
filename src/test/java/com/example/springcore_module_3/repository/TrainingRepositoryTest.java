package com.example.springcore_module_3.repository;


import com.example.springcore_module_3.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
public class TrainingRepositoryTest {

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private TrainingType trainingType;


    private Trainee createTrainee(String first, String last, String address, LocalDate birth) {
        User traineeUser = new User(first, last, first + "." + last, "hashedPw");
        testEntityManager.persist(traineeUser);
        return new Trainee(traineeUser, address, birth);
    }

    private Trainer createTrainer(String first, String last) {
        User trainerUser = new User(first, last, first + "." + last, "hashedPw");
        testEntityManager.persist(trainerUser);
        return new Trainer(trainerUser, trainingType);
    }

    @BeforeEach
    void beforeEach() {

        trainingType = testEntityManager.persist(new TrainingType("FITNESS"));

        Trainee trainee1 = createTrainee("John", "Doe", "1 Test st.", LocalDate.of(1990, 1, 1));
        Trainee trainee2 = createTrainee("Mike", "Douglas", "3 Test st.", LocalDate.of(1999, 6, 4));
        Trainer trainer1 = createTrainer("Sara", "Connor");
        Trainer trainer2 = createTrainer("Mike", "Wilson");
        testEntityManager.persist(trainee1);
        testEntityManager.persist(trainee2);
        testEntityManager.persist(trainer1);
        testEntityManager.persist(trainer2);

        testEntityManager.persist(new Training(
                trainee1,
                trainer1,
                "Training 1",
                trainingType,
                LocalDate.of(2026,10,5),
                Duration.ofMinutes(60)
        ));

        testEntityManager.persist(new Training(
                trainee2,
                trainer2,
                "Training 2",
                trainingType,
                LocalDate.of(2026,9,5),
                Duration.ofMinutes(60)
        ));

        testEntityManager.persist(new Training(
                trainee1,
                trainer2,
                "Training 3",
                trainingType,
                LocalDate.of(2026,11,10),
                Duration.ofMinutes(90)
        ));
        testEntityManager.flush();
    }


    @Test
    void findTraineeTrainings_filtersCorrectly() {
        List<Training> result = trainingRepository.findTraineeTrainings(
                "John.Doe",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 11, 11),
                null,
                trainingType);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals("Training 1", result.get(0).getTrainingName());
        assertEquals("Training 3", result.get(1).getTrainingName());

    }

    @Test
    void findTrainerTrainings_filtersCorrectly() {

        List<Training> result = trainingRepository.findTrainerTrainings(
                "Sara.Connor",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 20),
                "John.Doe");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Training 1", result.get(0).getTrainingName());
    }

    @Test
    @Transactional
    void deleteAllByTraineeUserUsernameTest(){

        trainingRepository.deleteAllByTraineeUserUsername("John.Doe");
        testEntityManager.flush();
        testEntityManager.clear();

        List<Training> result = trainingRepository.findTraineeTrainings("John.Doe", null, null, null, null);
        assertTrue(result.isEmpty());
    }

}
