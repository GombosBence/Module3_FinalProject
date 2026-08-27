package com.example.springcore_module_3.metrics;

import com.example.springcore_module_3.util.LoginAttemptTracker;
import com.example.springcore_module_3.util.TokenBlockList;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class GymMetrics {

    private final Counter traineeRegistrations;
    private final Counter trainerRegistrations;
    private final Counter failedAuthentications;

    public GymMetrics(MeterRegistry meterRegistry, TokenBlockList tokenBlockList, LoginAttemptTracker loginAttemptTracker) {

        this.traineeRegistrations = Counter.builder("gym.trainee.registrations")
                .description("Number of trainees registered")
                .register(meterRegistry);

        this.trainerRegistrations = Counter.builder("gym.trainer.registrations")
                .description("Number of trainers registered")
                .register(meterRegistry);

        this.failedAuthentications = Counter.builder("gym.authentication.failures")
                .description("Number of failed authentications")
                .register(meterRegistry);

        Gauge.builder("gym.token.blocklist.size", tokenBlockList, TokenBlockList::size)
                .description("Number of tokens blocked in memory")
                .register(meterRegistry);

        Gauge.builder("gym.login.attempt.tracked_usernames", loginAttemptTracker, LoginAttemptTracker::size)
                .description("Number of usernames currently tracked for failed login attempts (locked or pending)")
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
