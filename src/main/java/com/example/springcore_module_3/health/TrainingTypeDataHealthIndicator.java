package com.example.springcore_module_3.health;

import com.example.springcore_module_3.repository.TrainingTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class TrainingTypeDataHealthIndicator implements HealthIndicator {

    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeDataHealthIndicator(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }


    @Override
    public Health health() {
        long count = trainingTypeRepository.count();

        if (count == 0) {
            log.warn("Health check failed: no training types found");
            return Health.down()
                    .withDetail("trainingTypeCount", 0)
                    .withDetail("reason", "No training types found")
                    .build();
        }

        return  Health.up()
                .withDetail("trainingTypeCount", count)
                .build();
    }
}
