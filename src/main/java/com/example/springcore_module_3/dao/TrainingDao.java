package com.example.springcore_module_3.dao;

import com.example.springcore_module_3.model.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingDao {

    Training create(Training training);
    Optional<Training> findById(Long id);
    List<Training> findAllByTrainee(Long traineeId);
    List<Training> findAllByTrainer(Long trainerId);

}
