package com.example.springcore_module_3.dto;

import java.time.Duration;
import java.time.LocalDate;

public record TrainerTrainingDto (String trainingName, LocalDate date, TrainingTypeDto trainingType, Duration duration, String traineeName) {
}
