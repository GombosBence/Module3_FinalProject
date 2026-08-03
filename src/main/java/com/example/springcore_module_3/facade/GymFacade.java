package com.example.springcore_module_3.facade;

import com.example.springcore_module_3.dto.*;
import com.example.springcore_module_3.dto.request.AuthenticationRequest;
import com.example.springcore_module_3.model.*;
import com.example.springcore_module_3.service.AuthenticationService;
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
    private final AuthenticationService authenticationService;

    public GymFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService, AuthenticationService authenticationService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.authenticationService = authenticationService;
    }

    // --- Trainee ---

    public TraineeCreationResult createTrainee(String firstName, String lastName, String address, LocalDate dateOfBirth) {

        User user = new User(firstName, lastName, null, null);
        return traineeService.createTraineeProfile(user, address, dateOfBirth);
    }

    public Trainee updateTrainee(AuthenticationRequest credentials, Trainee trainee) {
        return traineeService.updateTraineeProfile(credentials, trainee);
    }

    public void activateTrainee(AuthenticationRequest credentials, String username) {
        traineeService.activateTraineeProfile(credentials, username);
    }

    public void deactivateTrainee(AuthenticationRequest credentials, String username) {
        traineeService.deactivateTraineeProfile(credentials, username);
    }

    public void deleteTrainee(AuthenticationRequest credentials, String username) {
        traineeService.deleteTraineeProfile(credentials, username);
    }

    public Trainee getTrainee(AuthenticationRequest credentials, Long id) {
        return traineeService.selectTraineeProfile(credentials, id);
    }

    public Trainee getTraineeByUsername(AuthenticationRequest credentials, String username) {
        return traineeService.selectTraineeProfileByUsername(credentials, username);
    }

    public List<Trainer> getUnassignedTrainers(AuthenticationRequest credentials, String traineeUsername) {
        return traineeService.selectUnassignedTrainers(credentials, traineeUsername);
    }

    public List<Trainer> updateTraineeTrainers(AuthenticationRequest credentials, String traineeUsername, List<String> usernames) {
        return traineeService.updateTraineeTrainers(credentials, traineeUsername, usernames);
    }

    // --- Trainer ---

    public TrainerCreationResult createTrainer(User user, TrainingType specialization) {
        return trainerService.createTrainerProfile(user, specialization);
    }

    public void updateTrainer(AuthenticationRequest credentials, Trainer trainer) {
        trainerService.updateTrainerProfile(credentials, trainer);
    }

    public void activateTrainer(AuthenticationRequest credentials, Long id) {
        trainerService.activateTrainerProfile(credentials, id);
    }

    public void deactivateTrainer(AuthenticationRequest credentials, Long id) {
        trainerService.deactivateTrainerProfile(credentials, id);
    }

    public Trainer getTrainer(AuthenticationRequest credentials, Long id) {
        return trainerService.selectTrainerProfile(credentials, id);
    }

    public Trainer getTrainerByUsername(AuthenticationRequest credentials, String username) {
        return trainerService.selectTrainerProfileByUsername(credentials, username);
    }

    // --- Training ---

    public Training createTraining(AuthenticationRequest credentials, Long traineeId, Long trainerId,
                                   String trainingName, TrainingType trainingType, LocalDate trainingDate,
                                   Duration trainingDuration) {
        return trainingService.createTraining(credentials, traineeId, trainerId, trainingName,
                trainingType, trainingDate, trainingDuration);
    }

    public List<Training> getTraineeTrainings(AuthenticationRequest credentials, String username,
                                              LocalDate fromDate, LocalDate toDate, String trainerName,
                                              TrainingType trainingType) {
        return trainingService.selectTraineeTrainings(credentials, username, fromDate, toDate, trainerName, trainingType);
    }

    public List<Training> getTrainerTrainings(AuthenticationRequest credentials, String username,
                                              LocalDate fromDate, LocalDate toDate, String traineeName) {
        return trainingService.selectTrainerTrainings(credentials, username, fromDate, toDate, traineeName);
    }

    // --- Auth ---
    public void changeUserPassword(String username, String oldPassword, String newPassword) {
        authenticationService.changePassword(username, oldPassword, newPassword);
    }

    public void login(String username, String password) {
        authenticationService.authenticate(username, password);
    }
}
