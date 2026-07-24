package com.example.springcore_module_3.config;

import com.example.springcore_module_3.configuration.AppConfig;
import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.Training;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    private final AppConfig appConfig = new AppConfig();

    @Test
    void traineeStorage_returnsEmptyConcurrentMap() {
        Map<Long, Trainee> storage = appConfig.traineeStorage();

        assertNotNull(storage);
        assertTrue(storage.isEmpty());
    }

    @Test
    void trainerStorage_returnsEmptyConcurrentMap() {
        Map<Long, Trainer> storage = appConfig.trainerStorage();

        assertNotNull(storage);
        assertTrue(storage.isEmpty());
    }

    @Test
    void trainingStorage_returnsEmptyConcurrentMap() {
        Map<Long, Training> storage = appConfig.trainingStorage();

        assertNotNull(storage);
        assertTrue(storage.isEmpty());
    }

    @Test
    void passwordEncoder_returnsWorkingBCryptEncoder() {
        BCryptPasswordEncoder encoder = appConfig.passwordEncoder();

        String hash = encoder.encode("rawPassword");

        assertNotEquals("rawPassword", hash);
        assertTrue(encoder.matches("rawPassword", hash));
    }
}