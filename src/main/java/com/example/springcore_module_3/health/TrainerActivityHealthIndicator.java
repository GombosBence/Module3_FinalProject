package com.example.springcore_module_3.health;

import com.example.springcore_module_3.repository.TrainerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TrainerActivityHealthIndicator implements HealthIndicator {

    private final TrainerRepository trainerRepository;

    public TrainerActivityHealthIndicator(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Override
    public Health health() {
        long trainers = trainerRepository.countByUserIsActive(true);

        if(trainers == 0){
            log.warn("Health check failed: No active trainers found");
            return Health.down()
                    .withDetail("activeTrainers", 0)
                    .build();
        }

        return Health.up()
                .withDetail("activeTrainers", trainers)
                .build();
    }
}
