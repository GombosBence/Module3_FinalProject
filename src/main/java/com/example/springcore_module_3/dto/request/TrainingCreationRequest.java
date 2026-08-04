package com.example.springcore_module_3.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.LocalDate;

public record TrainingCreationRequest(@NotBlank String traineeUsername, @NotBlank String trainerUsername,
                                      @NotBlank String trainingName, @NotNull LocalDate trainingDate,
                                      @NotNull Duration trainingDuration) {
}
