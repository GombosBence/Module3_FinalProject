package com.example.springcore_module_3.mapper;

import com.example.springcore_module_3.dto.TraineeTrainingDto;
import com.example.springcore_module_3.dto.TrainerTrainingDto;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.Training;

import java.util.List;

public class TrainingMapper {



    public static TraineeTrainingDto mapTrainingToTraineeTrainingDto(Training training) {

        return new TraineeTrainingDto(
                training.getTrainingName(),
                training.getTrainingDate(),
                TrainingTypeMapper.toDto(training.getTrainingType()),
                training.getTrainingDuration(),
                training.getTrainer().getUser().getUsername()
        );
    }

    public static TrainerTrainingDto mapTrainingToTrainerTrainingDto(Training training) {
        return new TrainerTrainingDto(
                training.getTrainingName(),
                training.getTrainingDate(),
                TrainingTypeMapper.toDto(training.getTrainingType()),
                training.getTrainingDuration(),
                training.getTrainee().getUser().getUsername()
        );
    }

    public static List<TraineeTrainingDto> mapToTraineeTrainingResponse(List<Training> trainings) {

        return trainings.stream().map(TrainingMapper::mapTrainingToTraineeTrainingDto).toList();
    }

    public static List<TrainerTrainingDto> mapToTrainerTrainingResponse(List<Training> trainings) {

        return trainings.stream().map(TrainingMapper::mapTrainingToTrainerTrainingDto).toList();
    }


}
