package com.example.springcore_module_3.config;

import com.example.springcore_module_3.configuration.AppConfig;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    private final AppConfig appConfig = new AppConfig();

    @Test
    void passwordEncoder_returnsWorkingBCryptEncoder() {
        BCryptPasswordEncoder encoder = appConfig.passwordEncoder();

        String hash = encoder.encode("password");

        assertNotEquals("password", hash);
        assertTrue(encoder.matches("password", hash));
    }
}