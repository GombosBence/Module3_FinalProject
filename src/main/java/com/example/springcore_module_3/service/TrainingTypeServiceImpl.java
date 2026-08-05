package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dto.request.AuthenticationRequest;
import com.example.springcore_module_3.model.TrainingType;
import com.example.springcore_module_3.repository.TrainingTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;

    private final AuthenticationService authenticationService;

    public TrainingTypeServiceImpl(TrainingTypeRepository trainingTypeRepository, AuthenticationService authenticationService) {
        this.trainingTypeRepository = trainingTypeRepository;
        this.authenticationService = authenticationService;
    }

    @Override
    public List<TrainingType> findAll(AuthenticationRequest credentials) {

        authenticationService.authenticate(credentials.username(), credentials.password());
        return trainingTypeRepository.findAll();
    }
}
