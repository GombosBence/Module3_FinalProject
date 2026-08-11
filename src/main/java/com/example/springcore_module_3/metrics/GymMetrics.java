package com.example.springcore_module_3.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class GymMetrics {

    private final Counter traineeRegistrations;
    private final Counter trainerRegistrations;
    private final Counter failedAuthentications;

    public GymMetrics(MeterRegistry meterRegistry) {

        this.traineeRegistrations = Counter.builder("gym.trainee.registrations")
                .description("Number of trainees registered")
                .register(meterRegistry);

        this.trainerRegistrations = Counter.builder("gym.trainer.registrations")
                .description("Number of trainers registered")
                .register(meterRegistry);

        this.failedAuthentications = Counter.builder("gym.authentication.failures")
                .description("Number of failed authentications")
                .register(meterRegistry);
    }

    public void incrementTraineeRegistrations() {
        traineeRegistrations.increment();
    }

    public void incrementTrainerRegistrations() {
        trainerRegistrations.increment();
    }

    public void incrementFailedAuthentications() {
        failedAuthentications.increment();
    }
}
