package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dto.request.AuthenticationRequest;
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
class TrainingTypeServiceImplTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private AuthenticationService authenticationService;

    private TrainingTypeServiceImpl trainingTypeService;

    @BeforeEach
    void setUp() {
        trainingTypeService = new TrainingTypeServiceImpl(trainingTypeRepository, authenticationService);
    }

    @Test
    void findAll_returnsListFromRepository_whenAuthenticated() {
        List<TrainingType> expected = List.of(new TrainingType("FITNESS"), new TrainingType("YOGA"));
        when(trainingTypeRepository.findAll()).thenReturn(expected);

        List<TrainingType> result = trainingTypeService.findAll(new AuthenticationRequest("John.Doe", "rawPw"));

        assertEquals(expected, result);
        verify(authenticationService).authenticate("John.Doe", "rawPw");
    }

    @Test
    void findAll_throws_whenAuthenticationFails() {
        doThrow(new com.example.springcore_module_3.exception.AuthenticationFailedException("Invalid username or password"))
                .when(authenticationService).authenticate("John.Doe", "wrongPw");

        assertThrows(com.example.springcore_module_3.exception.AuthenticationFailedException.class,
                () -> trainingTypeService.findAll(new AuthenticationRequest("John.Doe", "wrongPw")));

        verify(trainingTypeRepository, never()).findAll();
    }
}