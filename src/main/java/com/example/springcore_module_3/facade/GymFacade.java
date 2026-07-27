package com.example.springcore_module_3.facade;

import com.example.springcore_module_3.dto.*;
import com.example.springcore_module_3.model.*;
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
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    // --- Trainee ---

    public TraineeCreationResultDto createTrainee(User user, String address, LocalDate dateOfBirth) {
        return traineeService.createTraineeProfile(user, address, dateOfBirth);
    }

    public void updateTrainee(AuthenticationRequestDto credentials, Trainee trainee) {
        traineeService.updateTraineeProfile(credentials, trainee);
    }

    public void activateTrainee(AuthenticationRequestDto credentials, Long id) {
        traineeService.activateTraineeProfile(credentials, id);
    }

    public void deactivateTrainee(AuthenticationRequestDto credentials, Long id) {
        traineeService.deactivateTraineeProfile(credentials, id);
    }

    public void deleteTrainee(AuthenticationRequestDto credentials, String username) {
        traineeService.deleteTraineeProfile(credentials, username);
    }

    public Trainee getTrainee(AuthenticationRequestDto credentials, Long id) {
        return traineeService.selectTraineeProfile(credentials, id);
    }

    public Trainee getTraineeByUsername(AuthenticationRequestDto credentials, String username) {
        return traineeService.selectTraineeProfileByUsername(credentials, username);
    }

    public List<Trainer> getUnassignedTrainers(AuthenticationRequestDto credentials, String traineeUsername) {
        return traineeService.selectUnassignedTrainers(credentials, traineeUsername);
    }

    public void updateTraineeTrainers(AuthenticationRequestDto credentials, String traineeUsername, List<Long> trainerIds) {
        traineeService.updateTraineeTrainers(credentials, traineeUsername, trainerIds);
    }

    // --- Trainer ---

    public TrainerCreationResultDto createTrainer(User user, TrainingType specialization) {
        return trainerService.createTrainerProfile(user, specialization);
    }

    public void updateTrainer(AuthenticationRequestDto credentials, Trainer trainer) {
        trainerService.updateTrainerProfile(credentials, trainer);
    }

    public void activateTrainer(AuthenticationRequestDto credentials, Long id) {
        trainerService.activateTrainerProfile(credentials, id);
    }

    public void deactivateTrainer(AuthenticationRequestDto credentials, Long id) {
        trainerService.deactivateTrainerProfile(credentials, id);
    }

    public Trainer getTrainer(AuthenticationRequestDto credentials, Long id) {
        return trainerService.selectTrainerProfile(credentials, id);
    }

    public Trainer getTrainerByUsername(AuthenticationRequestDto credentials, String username) {
        return trainerService.selectTrainerProfileByUsername(credentials, username);
    }

    // --- Training ---

    public Training createTraining(AuthenticationRequestDto credentials, Long traineeId, Long trainerId,
                                   String trainingName, TrainingType trainingType, LocalDate trainingDate,
                                   Duration trainingDuration) {
        return trainingService.createTraining(credentials, traineeId, trainerId, trainingName,
                trainingType, trainingDate, trainingDuration);
    }

    public List<Training> getTraineeTrainings(AuthenticationRequestDto credentials, String username,
                                              LocalDate fromDate, LocalDate toDate, String trainerName,
                                              TrainingType trainingType) {
        return trainingService.selectTraineeTrainings(credentials, username, fromDate, toDate, trainerName, trainingType);
    }

    public List<Training> getTrainerTrainings(AuthenticationRequestDto credentials, String username,
                                              LocalDate fromDate, LocalDate toDate, String traineeName) {
        return trainingService.selectTrainerTrainings(credentials, username, fromDate, toDate, traineeName);
    }
}
