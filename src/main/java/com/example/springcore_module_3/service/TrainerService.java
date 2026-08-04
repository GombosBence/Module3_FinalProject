package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dto.request.AuthenticationRequest;
import com.example.springcore_module_3.dto.TrainerCreationResult;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.TrainingType;
import com.example.springcore_module_3.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface TrainerService {

    TrainerCreationResult createTrainerProfile(@NotNull User user, @NotNull TrainingType trainingType);
    Trainer updateTrainerProfile(@NotNull AuthenticationRequest credentials, @NotNull Trainer trainer);
    void deactivateTrainerProfile(@NotNull AuthenticationRequest credentials, @NotNull String username);
    void activateTrainerProfile(@NotNull AuthenticationRequest credentials, @NotNull String username);
    Trainer selectTrainerProfile(@NotNull AuthenticationRequest credentials, @NotNull Long id);
    Trainer selectTrainerProfileByUsername(@NotNull AuthenticationRequest credentials, @NotBlank String username);
}
