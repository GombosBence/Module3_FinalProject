package com.example.springcore_module_3.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordGeneratorTest {

    private final PasswordGenerator generator = new PasswordGenerator();

    @Test
    void generatePassword_returnsStringOfRequestedLength() {
        String password = generator.generatePassword(10);
        assertEquals(10, password.length());
    }

    @Test
    void generatePassword_respectsDifferentLengths() {
        assertEquals(5, generator.generatePassword(5).length());
        assertEquals(20, generator.generatePassword(20).length());
    }

    @Test
    void generatePassword_containsOnlyAllowedCharacters() {
        String password = generator.generatePassword(50);

        assertTrue(password.matches("[A-Za-z0-9]+"),
                "Password should only contain letters and digits, was: " + password);
    }

    @Test
    void generatePassword_isDifferentAcrossCalls() {
        String first = generator.generatePassword(10);
        String second = generator.generatePassword(10);

        assertNotEquals(first, second);
    }
}
