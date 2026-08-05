package com.example.springcore_module_3.facade;

import com.example.springcore_module_3.dto.*;
import com.example.springcore_module_3.dto.request.AuthenticationRequest;
import com.example.springcore_module_3.model.*;
import com.example.springcore_module_3.service.*;
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
    private final TrainingTypeService trainingTypeService;

    public GymFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService
            , AuthenticationService authenticationService, TrainingTypeService trainingTypeService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.authenticationService = authenticationService;
        this.trainingTypeService = trainingTypeService;
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

    public TrainerCreationResult createTrainer(String firstName, String lastName, TrainingType specialization) {

        User user = new User(firstName, lastName, null, null);
        return trainerService.createTrainerProfile(user, specialization);
    }

    public Trainer updateTrainer(AuthenticationRequest credentials, Trainer trainer) {
        return trainerService.updateTrainerProfile(credentials, trainer);
    }

    public void activateTrainer(AuthenticationRequest credentials, String username) {
        trainerService.activateTrainerProfile(credentials, username);
    }

    public void deactivateTrainer(AuthenticationRequest credentials, String username) {
        trainerService.deactivateTrainerProfile(credentials, username);
    }

    public Trainer getTrainer(AuthenticationRequest credentials, Long id) {
        return trainerService.selectTrainerProfile(credentials, id);
    }

    public Trainer getTrainerByUsername(AuthenticationRequest credentials, String username) {
        return trainerService.selectTrainerProfileByUsername(credentials, username);
    }

    // --- Training ---

    public void createTraining(AuthenticationRequest credentials, String traineeUsername, String trainerUsername,
                                   String trainingName, LocalDate trainingDate,
                                   Duration trainingDuration) {
        trainingService.createTraining(credentials, traineeUsername, trainerUsername, trainingName,
                 trainingDate, trainingDuration);
    }

    public List<Training> getTraineeTrainings(AuthenticationRequest credentials, String username,
                                              LocalDate fromDate, LocalDate toDate, String trainerName,
                                              String trainingType) {
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


    // --- TrainingType ---

    public List<TrainingType> getTrainingTypes(AuthenticationRequest credentials) {
        return trainingTypeService.findAll(credentials);
    }
}
