package com.example.springcore_module_3.service;

import com.example.springcore_module_3.model.TrainingType;
import com.example.springcore_module_3.repository.TrainingTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeServiceImpl(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Override
    public List<TrainingType> findAll() {
        return trainingTypeRepository.findAll();
    }
}
