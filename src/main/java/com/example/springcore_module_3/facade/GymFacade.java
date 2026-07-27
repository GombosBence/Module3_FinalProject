package com.example.springcore_module_3.facade;

import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.Training;
import com.example.springcore_module_3.model.TrainingType;
import com.example.springcore_module_3.service.TraineeService;
import com.example.springcore_module_3.service.TrainerService;
import com.example.springcore_module_3.service.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainingService trainingService;
    private final TrainerService trainerService;

    public GymFacade(TraineeService traineeService, TrainingService trainingService, TrainerService trainerService) {
        this.traineeService = traineeService;
        this.trainingService = trainingService;
        this.trainerService = trainerService;
        log.debug("GymFacade initialized with services");
    }

    //Trainee
    public Trainee createTrainee(String firstName, String lastName, String address, LocalDate dateOfBirth) {
        return traineeService.createTraineeProfile(firstName, lastName, address, dateOfBirth);
    }

    public void updateTrainee(Trainee trainee) {
        traineeService.updateTraineeProfile(trainee);
    }

    public void deleteTrainee(Long id) {
        traineeService.deleteTraineeProfile(id);
    }

    public Trainee getTraineeById(Long id) {
        return traineeService.selectTraineeProfile(id);
    }

    public Trainee getTraineeByUsername(String username) {
        return traineeService.selectTraineeProfileByUsername(username);
    }

    //Trainer
    public Trainer createTrainer(String firstName, String lastName, TrainingType trainingType) {
        return trainerService.createTrainerProfile(, firstName, trainingType);
    }

    public void updateTrainer(Trainer trainer) {
        trainerService.updateTrainerProfile(trainer);
    }

    public Trainer getTrainerById(Long id) {
        return trainerService.selectTrainerProfile(id);
    }

    public Trainer getTrainerByUsername(String username) {
        return trainerService.selectTrainerProfileByUsername(username);
    }

    //Training

    public Training createTraining(Long traineeId, Long trainerId, String trainingName, TrainingType trainingType, LocalDate trainingDate, Duration trainingDuration) {
        return trainingService.createTraining(traineeId, trainerId, trainingName, trainingType, trainingDate, trainingDuration);
    }

    public Training getTrainingById(Long id) {
        return trainingService.getTrainingById(id);
    }

    public List<Training> getTrainingsByTrainee(Long traineeId) {
        return trainingService.selectTraineeTrainings(traineeId, , , , );
    }


    public List<Training> getTrainingsByTrainerId(Long trainerId) {
        return trainingService.selectTrainerTrainings(trainerId, , , );
    }

    public void deactivateTrainee(Long id) {
        traineeService.deactivateTraineeProfile(id);
    }

}
