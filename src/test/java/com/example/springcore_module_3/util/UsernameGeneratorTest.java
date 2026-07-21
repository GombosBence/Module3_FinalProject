package com.example.springcore_module_3.util;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsernameGeneratorTest {

    private final UsernameGenerator generator = new UsernameGenerator();

    @Test
    void generateUsername_returnsBaseForm_whenNotTaken() {
        String username = generator.generateUsername("John", "Doe", candidate -> false);

        assertEquals("John.Doe", username);
    }

    @Test
    void generateUsername_appendsSuffix_whenBaseFormTaken() {
        Set<String> taken = Set.of("John.Doe");

        String username = generator.generateUsername("John", "Doe", taken::contains);

        assertEquals("John.Doe1", username);
    }

    @Test
    void generateUsername_incrementsSuffix_untilFree() {
        Set<String> taken = Set.of("John.Doe", "John.Doe1", "John.Doe2");

        String username = generator.generateUsername("John", "Doe", taken::contains);

        assertEquals("John.Doe3", username);
    }
}