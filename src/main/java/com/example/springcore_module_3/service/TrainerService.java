package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dto.AuthenticationRequestDto;
import com.example.springcore_module_3.dto.TrainerCreationResult;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.TrainingType;
import com.example.springcore_module_3.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface TrainerService {

    TrainerCreationResult createTrainerProfile(@NotNull User user, @NotNull TrainingType trainingType);
    void updateTrainerProfile(@NotNull AuthenticationRequestDto credentials, @NotNull Trainer trainer);
    void deactivateTrainerProfile(@NotNull AuthenticationRequestDto credentials, @NotNull Long id);
    void activateTrainerProfile(@NotNull AuthenticationRequestDto credentials, @NotNull Long id);
    Trainer selectTrainerProfile(@NotNull AuthenticationRequestDto credentials, @NotNull Long id);
    Trainer selectTrainerProfileByUsername(@NotNull AuthenticationRequestDto credentials, @NotBlank String username);
}
