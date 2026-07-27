package com.example.springcore_module_3.service;

import com.example.springcore_module_3.model.Training;
import com.example.springcore_module_3.model.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public interface TrainingService {

    Training createTraining(@NotNull Long traineeId, @NotNull Long trainerId, @NotBlank String trainingName,
                            @NotNull TrainingType trainingType, @Past LocalDate trainingDate, @NotNull Duration trainingDuration);

    List<Training> selectTraineeTrainings(@NotNull String username, LocalDate fromDate, LocalDate toDate, String trainerName, TrainingType trainingType);

    List<Training> selectTrainerTrainings(@NotNull String username,  LocalDate fromDate, LocalDate toDate, String traineeName);
}
