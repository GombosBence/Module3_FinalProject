package com.example.springcore_module_3.service;

import com.example.springcore_module_3.model.Training;
import com.example.springcore_module_3.model.TrainingType;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingService {

    Training createTraining(Long traineeId, Long trainerId, String trainingName,
                            TrainingType trainingType, LocalDate trainingDate, Duration trainingDuration);
    Training getTrainingById(Long id);
    List<Training> selectAllTrainingsByTrainee(Long id);
    List<Training> selectAllTrainingsByTrainer(Long id);

}
