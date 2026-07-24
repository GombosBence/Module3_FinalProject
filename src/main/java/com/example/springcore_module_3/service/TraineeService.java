package com.example.springcore_module_3.service;

import com.example.springcore_module_3.model.Trainee;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public interface TraineeService {

    Trainee createTraineeProfile(@NotBlank String firstName, @NotBlank String lastName, @NotBlank String address, @Past LocalDate dateOfBirth);
    void updateTraineeProfile(@NotNull Trainee trainee);
    void deactivateTraineeProfile(@NotNull Long id);
    void deleteTraineeProfile(@NotNull Long id);
    Trainee selectTraineeProfile(@NotNull Long id);
    Trainee selectTraineeProfileByUsername(@NotBlank String username);

}
