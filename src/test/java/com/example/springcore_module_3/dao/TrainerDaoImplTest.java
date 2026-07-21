package com.example.springcore_module_3.dao;

import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TrainerDaoImplTest {

    private Map<Long, Trainer> storage;
    private TrainerDaoImpl dao;

    @BeforeEach
    void setUp()
    {
        storage = new HashMap<>();
        dao = new TrainerDaoImpl();
        dao.setStorage(storage);
        dao.init();
    }

    private Trainer sampleTrainer(String firstname, String lastname)
    {
        Trainer trainer =  new Trainer();
        trainer.setFirstName(firstname);
        trainer.setLastName(lastname);
        trainer.setUsername(firstname + "." + lastname);
        trainer.setPassword("password12");
        trainer.setActive(true);
        trainer.setSpecialization(TrainingType.FITNESS);
        return trainer;
    }

    @Test
    void createTrainerTest(){
        Trainer trainer = sampleTrainer("John", "Smith");

        Trainer created =  dao.create(trainer);

        assertTrue(storage.containsKey(1L));
        assertEquals(1L, created.getUserId());
        assertEquals(trainer, storage.get(1L));
    }

    @Test
    void updateTrainerSuccessTest(){
        Trainer trainer = dao.create(sampleTrainer("John", "Smith"));
        trainer.setFirstName("Tom");

        boolean updated = dao.update(trainer);

        assertTrue(updated);
        assertEquals(storage.get(1L).getFirstName(), trainer.getFirstName());
    }

    @Test
    void updateTrainerFailTest(){
        Trainer trainer = sampleTrainer("John", "Smith");

        boolean updated = dao.update(trainer);

        assertFalse(updated);
        assertTrue(storage.isEmpty());
    }

    @Test
    void findTrainerByIdSuccessTest(){
        Trainer trainer = dao.create(sampleTrainer("John", "Smith"));

        Optional<Trainer> found = dao.findById(trainer.getUserId());

        assertTrue(found.isPresent());
    }

    @Test
    void findTrainerByIdFailureTest(){
        Trainer trainer = sampleTrainer("John", "Smith");
        trainer.setUserId(999L);

        Optional<Trainer> found = dao.findById(trainer.getUserId());

        assertFalse(found.isPresent());
    }

    @Test
    void findTrainerByUsernameSuccessTest(){
        Trainer trainer = dao.create(sampleTrainer("John", "Smith"));

        Optional<Trainer> found = dao.findByUsername(trainer.getUsername());

        assertTrue(found.isPresent());
    }

    @Test
    void findTrainerByUsernameFailureTest(){
        Trainer trainer = sampleTrainer("John", "Smith");

        Optional<Trainer> found = dao.findByUsername(trainer.getUsername());

        assertFalse(found.isPresent());
    }

    @Test
    void findAllTrainersTest(){
        Trainer trainer1 = dao.create(sampleTrainer("John", "Smith"));
        Trainer trainer2 = dao.create(sampleTrainer("John", "Doe"));
        Trainer trainer3 = dao.create(sampleTrainer("Thomas", "Anderson"));

        List<Trainer> found = dao.findAll();

        assertTrue(found.contains(trainer1));
        assertTrue(found.contains(trainer2));
        assertTrue(found.contains(trainer3));
        assertEquals(3, found.size());
    }

}
