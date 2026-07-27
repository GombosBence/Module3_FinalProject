package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dto.TrainerCreationResultDto;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.TrainingType;
import com.example.springcore_module_3.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface TrainerService {

    TrainerCreationResultDto createTrainerProfile(@NotNull User user, @NotNull TrainingType trainingType);
    void updateTrainerProfile(@NotNull Trainer trainer);
    void deactivateTrainerProfile(@NotNull Long id);
    void activateTrainerProfile(@NotNull Long id);
    Trainer selectTrainerProfile(@NotNull Long id);
    Trainer selectTrainerProfileByUsername(@NotBlank String username);
}
