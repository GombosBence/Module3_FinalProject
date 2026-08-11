package com.example.springcore_module_3.health;

import com.example.springcore_module_3.repository.TrainingTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingTypeDataHealthIndicatorTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypeDataHealthIndicator trainingTypeDataHealthIndicator;

    @Test
    void healthIndicator_ReportsUp_whenTrainingTypeIsPresent() {

        when(trainingTypeRepository.count()).thenReturn(5L);

        Health result = trainingTypeDataHealthIndicator.health();

        assert result != null;
        assertEquals(Status.UP, result.getStatus());
        assertEquals(5L, result.getDetails().get("trainingTypeCount"));
    }


    @Test
    void healthIndicator_ReportsDown_whenTrainingTypeIsNotPresent() {
        when(trainingTypeRepository.count()).thenReturn(0L);

        Health result = trainingTypeDataHealthIndicator.health();

        assert result != null;
        assertEquals(Status.DOWN, result.getStatus());
    }

}
