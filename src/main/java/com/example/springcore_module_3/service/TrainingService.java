package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dto.request.AuthenticationRequest;
import com.example.springcore_module_3.model.Training;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public interface TrainingService {

    Training createTraining(@NotNull String traineeUsername,
                            @NotNull String trainerUsername, @NotBlank String trainingName,
                            @Past LocalDate trainingDate,
                            @NotNull Duration trainingDuration);


    List<Training> selectTraineeTrainings(String username,
                                          LocalDate fromDate, LocalDate toDate,
                                          String trainerName, String trainingType);

    List<Training> selectTrainerTrainings(@NotNull String username, LocalDate fromDate, LocalDate toDate, String traineeName);
}
