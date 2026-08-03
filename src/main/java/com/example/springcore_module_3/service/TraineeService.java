package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dto.request.AuthenticationRequest;
import com.example.springcore_module_3.dto.TraineeCreationResult;
import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.util.List;

public interface TraineeService {

    TraineeCreationResult createTraineeProfile(@NotNull User user, @NotBlank String address, @Past LocalDate dateOfBirth);
    Trainee updateTraineeProfile(@NotNull AuthenticationRequest credentials, @NotNull Trainee trainee);
    void deactivateTraineeProfile(@NotNull AuthenticationRequest credentials, @NotNull String username);
    void activateTraineeProfile(@NotNull AuthenticationRequest credentials, @NotNull String username);
    void deleteTraineeProfile(@NotNull AuthenticationRequest credentials, @NotNull String username);
    Trainee selectTraineeProfile(@NotNull AuthenticationRequest credentials, @NotNull Long id);
    Trainee selectTraineeProfileByUsername(@NotNull AuthenticationRequest credentials, @NotBlank String username);
    List<Trainer> selectUnassignedTrainers(@NotNull AuthenticationRequest credentials, String username);
    List<Trainer> updateTraineeTrainers(@NotNull AuthenticationRequest credentials, @NotNull String username, @NotNull List<String> usernames);

}
