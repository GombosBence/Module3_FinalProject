package com.example.springcore_module_3.metrics;

import com.example.springcore_module_3.util.LoginAttemptTracker;
import com.example.springcore_module_3.util.TokenBlockList;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GymMetricsTest {

    private MeterRegistry registry;
    private GymMetrics gymMetrics;
    private TokenBlockList tokenBlockList;
    private LoginAttemptTracker loginAttemptTracker;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        gymMetrics = new GymMetrics(registry, tokenBlockList, loginAttemptTracker);
    }

    @Test
    void incrementTraineeRegistrations_increasesCounterByOne() {
        gymMetrics.incrementTraineeRegistrations();
        gymMetrics.incrementTraineeRegistrations();

        double count = registry.counter("gym.trainee.registrations").count();

        assertEquals(2.0, count);
    }

    @Test
    void incrementFailedAuthentications_increasesCounterByOne() {
        gymMetrics.incrementFailedAuthentications();

        double count = registry.counter("gym.authentication.failures").count();

        assertEquals(1.0, count);
    }
}