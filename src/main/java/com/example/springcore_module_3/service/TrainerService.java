package com.example.springcore_module_3.service;

import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.TrainingType;

public interface TrainerService {

    Trainer createTrainerProfile(String firstName, String lastName, TrainingType trainingType);
    void updateTrainerProfile(Trainer trainer);
    Trainer selectTrainerProfile(Long id);
    Trainer selectTrainerProfileByUsername(String username);
}
