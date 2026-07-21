package com.example.springcore_module_3.config;

import com.example.springcore_module_3.configuration.StorageInitializer;
import com.example.springcore_module_3.model.Trainee;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StorageInitializerTest {

    @Test
    void postProcessAfterInitialization_loadsCorrectly() {

        StorageInitializer initializer = new StorageInitializer();
        ReflectionTestUtils.setField(initializer, "traineeDataFile", "trainee-data.txt");

        Map<Long, Trainee> storage = new HashMap<>();
        initializer.postProcessBeforeInitialization(storage, "traineeStorage");

        assertFalse(storage.isEmpty());
        Trainee first = storage.get(1L);
        assertNotNull(first);
        assertEquals("John", first.getFirstName());
    }

    @Test
    void postProcessInitialization_ignoresUnrelatedBeans(){
        Object otherBean = new Object();
        Object result = new StorageInitializer().postProcessAfterInitialization(otherBean, "otherBean");

        assertSame(otherBean, result);
    }

}
