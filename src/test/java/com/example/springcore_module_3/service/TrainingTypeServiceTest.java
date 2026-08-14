package com.example.springcore_module_3.service;

import com.example.springcore_module_3.model.TrainingType;
import com.example.springcore_module_3.repository.TrainingTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    private TrainingTypeServiceImpl trainingTypeService;

    @BeforeEach
    void setUp() {
        trainingTypeService = new TrainingTypeServiceImpl(trainingTypeRepository);
    }

    @Test
    void findAll_returnsListFromRepository_whenAuthenticated() {
        List<TrainingType> expected = List.of(new TrainingType("FITNESS"), new TrainingType("YOGA"));
        when(trainingTypeRepository.findAll()).thenReturn(expected);

        List<TrainingType> result = trainingTypeService.findAll();

        assertEquals(expected, result);
    }
}