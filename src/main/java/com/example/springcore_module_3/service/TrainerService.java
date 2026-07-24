package com.example.springcore_module_3.service;

import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface TrainerService {

    Trainer createTrainerProfile(@NotBlank String firstName, @NotBlank String lastName, @NotNull TrainingType trainingType);
    void updateTrainerProfile(@NotNull Trainer trainer);
    Trainer selectTrainerProfile(@NotNull Long id);
    Trainer selectTrainerProfileByUsername(@NotBlank String username);
}
