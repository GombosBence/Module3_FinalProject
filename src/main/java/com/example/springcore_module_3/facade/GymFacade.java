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

    public Trainee updateTrainee(Trainee trainee) {
        return traineeService.updateTraineeProfile(trainee);
    }

    public void activateTrainee(String username) {
        traineeService.activateTraineeProfile(username);
    }

    public void deactivateTrainee(String username) {
        traineeService.deactivateTraineeProfile(username);
    }

    public void deleteTrainee(String username) {
        traineeService.deleteTraineeProfile(username);
    }

    public Trainee getTrainee(Long id) {
        return traineeService.selectTraineeProfile(id);
    }

    public Trainee getTraineeByUsername(String username) {
        return traineeService.selectTraineeProfileByUsername(username);
    }

    public List<Trainer> getUnassignedTrainers(String traineeUsername) {
        return traineeService.selectUnassignedTrainers(traineeUsername);
    }

    public List<Trainer> updateTraineeTrainers(String traineeUsername, List<String> usernames) {
        return traineeService.updateTraineeTrainers(traineeUsername, usernames);
    }

    // --- Trainer ---

    public TrainerCreationResult createTrainer(String firstName, String lastName, TrainingType specialization) {

        User user = new User(firstName, lastName, null, null);
        return trainerService.createTrainerProfile(user, specialization);
    }

    public Trainer updateTrainer(Trainer trainer) {
        return trainerService.updateTrainerProfile(trainer);
    }

    public void activateTrainer(String username) {
        trainerService.activateTrainerProfile(username);
    }

    public void deactivateTrainer(String username) {
        trainerService.deactivateTrainerProfile(username);
    }

    public Trainer getTrainer(Long id) {
        return trainerService.selectTrainerProfile(id);
    }

    public Trainer getTrainerByUsername(String username) {
        return trainerService.selectTrainerProfileByUsername(username);
    }

    // --- Training ---

    public void createTraining(String traineeUsername, String trainerUsername,
                                   String trainingName, LocalDate trainingDate,
                                   Duration trainingDuration) {
        trainingService.createTraining(traineeUsername, trainerUsername, trainingName,
                 trainingDate, trainingDuration);
    }

    public List<Training> getTraineeTrainings(String username, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingType) {
        return trainingService.selectTraineeTrainings( username, fromDate, toDate, trainerName, trainingType);
    }

    public List<Training> getTrainerTrainings(String username, LocalDate fromDate, LocalDate toDate, String traineeName) {
        return trainingService.selectTrainerTrainings(username, fromDate, toDate, traineeName);
    }

    // --- Auth ---
    public void changeUserPassword(String username, String oldPassword, String newPassword) {
        authenticationService.changePassword(username, oldPassword, newPassword);
    }

    public void login(String username, String password) {
        authenticationService.authenticate(username, password);
    }


    // --- TrainingType ---

    public List<TrainingType> getTrainingTypes() {
        return trainingTypeService.findAll();
    }
}
