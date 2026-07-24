package com.example.springcore_module_3.service;

import com.example.springcore_module_3.model.Training;
import com.example.springcore_module_3.model.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingService {

    Training createTraining(@NotNull Long traineeId, @NotNull Long trainerId, @NotBlank String trainingName,
                            @NotNull TrainingType trainingType, @Past LocalDate trainingDate, @NotNull Duration trainingDuration);
    Training getTrainingById(@NotNull Long id);
    List<Training> selectAllTrainingsByTrainee(@NotNull Long id);
    List<Training> selectAllTrainingsByTrainer(@NotNull Long id);

}
