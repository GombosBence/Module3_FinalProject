package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dto.AuthenticationRequestDto;
import com.example.springcore_module_3.dto.TraineeCreationResultDto;
import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public interface TraineeService {

    TraineeCreationResultDto createTraineeProfile(@NotNull User user, @NotBlank String address, @Past LocalDate dateOfBirth);
    void updateTraineeProfile(@NotNull AuthenticationRequestDto credentials, @NotNull Trainee trainee);
    void deactivateTraineeProfile(@NotNull AuthenticationRequestDto credentials, @NotNull Long id);
    void activateTraineeProfile(@NotNull AuthenticationRequestDto credentials,@NotNull Long id);
    void deleteTraineeProfile(@NotNull AuthenticationRequestDto credentials,@NotNull String username);
    Trainee selectTraineeProfile(@NotNull AuthenticationRequestDto credentials,@NotNull Long id);
    Trainee selectTraineeProfileByUsername(@NotNull AuthenticationRequestDto credentials,@NotBlank String username);

}
