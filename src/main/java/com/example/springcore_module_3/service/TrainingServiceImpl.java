package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dto.AuthenticationRequestDto;
import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.Training;
import com.example.springcore_module_3.model.TrainingType;
import com.example.springcore_module_3.repository.TraineeRepository;
import com.example.springcore_module_3.repository.TrainerRepository;
import com.example.springcore_module_3.repository.TrainingRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final AuthenticationService authenticationService;

    public TrainingServiceImpl(TrainingRepository trainingRepository,  TraineeRepository traineeRepository, TrainerRepository trainerRepository,
                               AuthenticationService authenticationService) {
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.authenticationService = authenticationService;
    }

    @Override
    @Transactional
    public Training createTraining(@NotNull AuthenticationRequestDto credentials, Long traineeId, Long trainerId, String trainingName, TrainingType trainingType, LocalDate trainingDate, Duration trainingDuration) {

        authenticationService.authenticate(credentials.username(), credentials.password());

        Trainee trainee = traineeRepository.findById(traineeId).orElseThrow(() -> {
            log.warn("Trainee with id {} not found", traineeId);
            return new NoSuchElementException("Trainee with id " + traineeId + " not found");
        });

        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow(() -> {
            log.warn("Trainer with id {} not found", trainerId);
            return new NoSuchElementException("Trainer with id " + trainerId + " not found");
        });

        Training newTraining  = new Training(trainee, trainer, trainingName, trainingType, trainingDate, trainingDuration);
        trainingRepository.save(newTraining);
        log.info("Training has been created with name={}", trainingName);
        return newTraining;
    }

    @Override
    public List<Training> selectTraineeTrainings(@NotNull AuthenticationRequestDto credentials, String username, LocalDate fromDate, LocalDate toDate, String trainerName, TrainingType trainingType) {
        authenticationService.authenticate(credentials.username(), credentials.password());
        return trainingRepository.findTraineeTrainings(username, fromDate, toDate, trainerName, trainingType);
    }

    @Override
    public List<Training> selectTrainerTrainings(@NotNull AuthenticationRequestDto credentials, String username, LocalDate fromDate, LocalDate toDate, String traineeName) {
        authenticationService.authenticate(credentials.username(), credentials.password());
        return trainingRepository.findTrainerTrainings(username, fromDate, toDate, traineeName);
    }
}
