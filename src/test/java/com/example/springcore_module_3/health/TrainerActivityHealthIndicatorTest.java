package com.example.springcore_module_3.health;

import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.repository.TrainerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerActivityHealthIndicatorTest{

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TrainerActivityHealthIndicator trainerActivityHealthIndicator;


    @Test
    public void healthIndicator_reportsUp_whenActiveTrainerIsPresent(){

        when(trainerRepository.countByUserIsActive(true)).thenReturn(3L);

        Health result = trainerActivityHealthIndicator.health();

        assert result != null;
        assertEquals(Status.UP, result.getStatus());
        assertEquals(3L, result.getDetails().get("activeTrainers"));
    }

    @Test
    public void healthIndicator_reportsDown_whenActiveTrainerIsNotPresent(){

        when(trainerRepository.countByUserIsActive(true)).thenReturn(0L);

        Health result = trainerActivityHealthIndicator.health();

        assert result != null;
        assertEquals(Status.DOWN, result.getStatus());
    }
}
