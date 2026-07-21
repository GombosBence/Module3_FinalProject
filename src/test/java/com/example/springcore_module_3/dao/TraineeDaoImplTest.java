package com.example.springcore_module_3.dao;

import com.example.springcore_module_3.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TraineeDaoImplTest {

    private Map<Long, Trainee> storage;
    private TraineeDaoImpl dao;

    @BeforeEach
    void setUp() {
        storage = new HashMap<>();
        dao = new TraineeDaoImpl();
        dao.setStorage(storage);
        dao.init();
    }

    private Trainee sampleTrainee(String firstName, String lastName) {
        Trainee trainee = new Trainee();
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setUsername(firstName + "." + lastName);
        trainee.setPassword("password12");
        trainee.setActive(true);
        trainee.setDateOfBirth(LocalDate.of(1980, 1, 1));
        trainee.setAddress("Budapest main street 10.");
        return trainee;
    }

    @Test
    void createTraineeTest(){
        Trainee trainee = sampleTrainee("John", "Smith");
        Trainee created = dao.create(trainee);

        assertEquals(1L, created.getUserId());
        assertTrue(storage.containsKey(1L));
        assertEquals(trainee, storage.get(1L));
    }

    @Test
    void create_doesntReuseId_afterDeleteMiddleRecord(){
        Trainee trainee1 = dao.create(sampleTrainee("John", "Smith"));
        Trainee trainee2 = dao.create(sampleTrainee("John", "Doe"));
        Trainee trainee3 = dao.create(sampleTrainee("Thomas", "Anderson"));

        Optional<Trainee> deleted = dao.delete(trainee2.getUserId());
        Trainee trainee4 = dao.create(sampleTrainee("New", "Trainee"));

        assertTrue(deleted.isPresent());
        assertEquals(4L, trainee4.getUserId());
        assertFalse(storage.containsKey(2L));
    }

    @Test
    void updateTraineeSuccessTest(){
        Trainee trainee = dao.create(sampleTrainee("John", "Smith"));
        trainee.setAddress("New address");

        boolean updated = dao.update(trainee);

        assertTrue(updated);
        assertEquals(1L, trainee.getUserId());
        assertEquals("New address", storage.get(trainee.getUserId()).getAddress());
    }

    @Test
    void updateTraineeFailureTest(){
        Trainee trainee = sampleTrainee("John", "Smith");

        boolean updated = dao.update(trainee);

        assertFalse(updated);
        assertTrue(storage.isEmpty());
    }

    @Test
    void deleteTraineeSuccessTest(){
        Trainee trainee = dao.create(sampleTrainee("John", "Smith"));

        Optional<Trainee> deleted = dao.delete(trainee.getUserId());

        assertTrue(deleted.isPresent());
        assertEquals(deleted.get(), trainee);
        assertTrue(storage.isEmpty());
    }

    @Test
    void deleteTraineeFailureTest(){
        Optional<Trainee> deleted = dao.delete(999L);

        assertFalse(deleted.isPresent());
        assertTrue(storage.isEmpty());
    }

    @Test
    void getTraineeByIdSuccessTest(){
        Trainee trainee = dao.create(sampleTrainee("John", "Smith"));

        Optional<Trainee> found = dao.findById(trainee.getUserId());

        assertTrue(found.isPresent());
        assertEquals(found.get(), trainee);
    }

    @Test
    void getTraineeByIdFailureTest(){
        Optional<Trainee> found = dao.findById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    void getTraineeByUsernameTest(){
        Trainee trainee = dao.create(sampleTrainee("John", "Smith"));

        Optional<Trainee> found = dao.findByUsername(trainee.getUsername());

        assertTrue(found.isPresent());
        assertEquals(found.get(), trainee);
    }

    @Test
    void getTraineeByUsernameFailureTest(){
        Trainee trainee = sampleTrainee("John", "Smith");

        Optional<Trainee> found = dao.findByUsername(trainee.getUsername());

        assertFalse(found.isPresent());
    }

    @Test
    void findAllTraineeTest(){
        Trainee trainee1 = dao.create(sampleTrainee("John", "Smith"));
        Trainee trainee2 = dao.create(sampleTrainee("John", "Doe"));
        Trainee trainee3 = dao.create(sampleTrainee("Thomas", "Anderson"));

        List<Trainee> found = dao.findAll();

        assertTrue(found.contains(trainee1));
        assertTrue(found.contains(trainee2));
        assertTrue(found.contains(trainee3));
        assertEquals(3, found.size());
    }

}
