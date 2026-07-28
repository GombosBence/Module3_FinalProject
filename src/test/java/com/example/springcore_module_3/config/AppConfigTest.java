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
    void passwordEncoder_returnsWorkingBCryptEncoder() {
        BCryptPasswordEncoder encoder = appConfig.passwordEncoder();

        String hash = encoder.encode("rawPassword");

        assertNotEquals("rawPassword", hash);
        assertTrue(encoder.matches("rawPassword", hash));
    }
}