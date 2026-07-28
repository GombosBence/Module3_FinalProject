package com.example.springcore_module_3.repository;


import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class TraineeRepositoryTest {

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Trainee trainee;

    @BeforeEach
    public void setup() {

        User traineeUser = new User("John", "Doe", "John.Doe", "hashedPw");
        trainee = new Trainee(traineeUser, "1 Test st.", LocalDate.of(1990, 1, 1));
        testEntityManager.persist(trainee);

    }

    @Test
    void findByUserUsernameTest() {

        Optional<Trainee> traineeOptional = traineeRepository.findByUserUsername(trainee.getUser().getUsername());

        assertTrue(traineeOptional.isPresent());
        assertEquals(trainee.getUser().getUsername(), traineeOptional.get().getUser().getUsername());
        assertEquals(trainee.getAddress(), traineeOptional.get().getAddress());
    }
}
