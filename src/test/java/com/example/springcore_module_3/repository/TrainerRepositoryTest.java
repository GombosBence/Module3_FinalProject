package com.example.springcore_module_3.repository;

import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.TrainingType;
import com.example.springcore_module_3.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TrainerRepositoryTest{

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private TrainingType trainingType;
    private Trainer assignedTrainer;
    private Trainer unassignedTrainer;
    private Trainee trainee;

    @BeforeEach
    public void setup() {
        trainingType = testEntityManager.persist(new TrainingType("FITNESS"));

        User traineeUser = testEntityManager.persist(new User("Jane", "Roe", "Jane.Roe", "hash1"));
        trainee = new Trainee(traineeUser, "1 Test St", LocalDate.of(2000,1,1));

        User assignedTrainerUser = testEntityManager.persist(new User("Mike", "Wilson", "Mike.Wilson", "hash2"));
        assignedTrainer = new Trainer(assignedTrainerUser, trainingType);
        testEntityManager.persist(assignedTrainer);

        User unassignedTrainerUser = testEntityManager.persist(new User("Sara", "Connor", "Sara.Connor", "hash3"));
        unassignedTrainer = new Trainer(unassignedTrainerUser, trainingType);
        testEntityManager.persist(unassignedTrainer);

        trainee.getTrainers().add(assignedTrainer);
        testEntityManager.persist(trainee);

        testEntityManager.flush();
    }


    @Test
    void findUnassignedTrainers_excludesAlreadyAssignedTrainers() {
        List<Trainer> trainers = trainerRepository.findUnassignedTrainers("Jane.Roe");

        assertEquals(1, trainers.size());
        assertEquals("Sara.Connor", trainers.get(0).getUser().getUsername());
    }

    @Test
    void findByUsernameTrainerTest(){
        Optional<Trainer> trainer = trainerRepository.findByUserUsername("Mike.Wilson");

        assertTrue(trainer.isPresent());
        assertEquals("Mike.Wilson", trainer.get().getUser().getUsername());
        assertEquals(trainingType.getTrainingTypeId(), trainer.get().getSpecialization().getTrainingTypeId());

    }

    @Test
    void updateTraineeTrainers_persistenceTest(){

        assertEquals(1, trainee.getTrainers().size());

        trainee.setTrainers(new ArrayList<>(List.of(unassignedTrainer)));
        testEntityManager.persist(trainee);
        testEntityManager.flush();
        testEntityManager.clear();

        Trainee reloaded = testEntityManager.find(Trainee.class, trainee.getTraineeId());
        assert reloaded != null;
        List<String> trainerUsernames = reloaded.getTrainers().stream().map(t -> t.getUser().getUsername()).toList();

        assertEquals(1, trainerUsernames.size());
        assertTrue(trainerUsernames.contains("Sara.Connor"));
        assertFalse(trainerUsernames.contains("Mike.Wilson"));
    }

    @Test
    void addNewTraineeTrainer_persistenceTest(){

        trainee.getTrainers().add(unassignedTrainer);
        testEntityManager.persist(trainee);
        testEntityManager.flush();
        testEntityManager.clear();

        Trainee reloaded = testEntityManager.find(Trainee.class, trainee.getTraineeId());

        assert reloaded != null;
        assertEquals(2, reloaded.getTrainers().size());
        assertTrue(reloaded.getTrainers().contains(unassignedTrainer));
        assertTrue(reloaded.getTrainers().contains(assignedTrainer));
    }

}
