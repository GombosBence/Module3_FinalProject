package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dto.request.AuthenticationRequest;
import com.example.springcore_module_3.dto.TraineeCreationResult;
import com.example.springcore_module_3.exception.InvalidStateTransitionException;
import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.User;
import com.example.springcore_module_3.repository.TraineeRepository;
import com.example.springcore_module_3.repository.TrainerRepository;
import com.example.springcore_module_3.repository.TrainingRepository;
import com.example.springcore_module_3.repository.UserRepository;
import com.example.springcore_module_3.util.PasswordGenerator;
import com.example.springcore_module_3.util.UsernameGenerator;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;
    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;
    private final TrainerRepository trainerRepository;


    public TraineeServiceImpl(TraineeRepository traineeRepository, PasswordGenerator passwordGenerator,
                              UsernameGenerator usernameGenerator, PasswordEncoder passwordEncoder,
                              AuthenticationService authenticationService, TrainingRepository trainingRepository, UserRepository userRepository,
                              TrainerRepository trainerRepository) {
        this.traineeRepository = traineeRepository;
        this.passwordGenerator = passwordGenerator;
        this.usernameGenerator = usernameGenerator;
        this.passwordEncoder = passwordEncoder;
        this.trainingRepository = trainingRepository;
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.trainerRepository = trainerRepository;
    }

    @Override
    public TraineeCreationResult createTraineeProfile(User user, String address, LocalDate dateOfBirth) {
        String password = passwordGenerator.generatePassword(10);
        String username = usernameGenerator.generateUsername(user.getFirstName(), user.getLastName(),
                userRepository::existsByUsername);

        String hashPassword = passwordEncoder.encode(password);
        user.setPassword(hashPassword);
        user.setUsername(username);
        Trainee newTrainee = new Trainee(user, address, dateOfBirth);
        traineeRepository.save(newTrainee);
        log.info("Trainee created with username: {}", username);
        return new TraineeCreationResult(newTrainee, password);
    }

    @Override
    @Transactional
    public Trainee updateTraineeProfile(@NotNull AuthenticationRequest credentials, Trainee trainee) {

        Trainee original = traineeRepository.findByUserUsername(trainee.getUser().getUsername()).orElseThrow(() ->{
            log.warn("Trainee not found with username: {}", trainee.getUser().getUsername());
            return new NoSuchElementException("Trainee not found");});

        authenticationService.authenticateAndAuthorize(credentials.username(), credentials.password(), trainee.getUser().getUsername());

        if(trainee.getUser().getFirstName() != null) original.getUser().setFirstName(trainee.getUser().getFirstName());
        if(trainee.getUser().getLastName() != null) original.getUser().setLastName(trainee.getUser().getLastName());
        if(trainee.getAddress() != null) original.setAddress(trainee.getAddress());
        if(trainee.getDateOfBirth() != null) original.setDateOfBirth(trainee.getDateOfBirth());
        original.getUser().setActive(trainee.getUser().isActive());

        return traineeRepository.save(original);
    }

    @Override
    @Transactional
    public void deactivateTraineeProfile(@NotNull AuthenticationRequest credentials, String username) {

        authenticationService.authenticate(credentials.username(), credentials.password());
        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    log.warn("Attempted to deactivate non-existing trainee username={}", username);
                    return new NoSuchElementException("Trainee with username " + username + " does not exist");
                });


        if(!trainee.getUser().isActive()) {
            log.warn("Attempted to deactivate already inactive trainee username={}", username);
            throw new InvalidStateTransitionException("Trainee already inactive " + trainee.getUser().getUsername());
        }

        trainee.getUser().setActive(false);
        traineeRepository.save(trainee);
        log.info("Trainee username={} deactivated", username);
    }

    @Override
    @Transactional
    public void activateTraineeProfile(@NotNull AuthenticationRequest credentials, String username) {

        authenticationService.authenticate(credentials.username(), credentials.password());
        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    log.warn("Attempted to activate non-existing trainee username={}", username);
                    return new NoSuchElementException("Trainee with username " + username + " does not exist");
                });


        if(trainee.getUser().isActive()) {
            log.warn("Attempted to activate already active trainee username={}", username);
            throw new InvalidStateTransitionException("Trainee already active " + trainee.getUser().getUsername());
        }

        trainee.getUser().setActive(true);
        traineeRepository.save(trainee);
        log.info("Trainee username={} activated", username);
    }

    @Override
    @Transactional
    public void deleteTraineeProfile(@NotNull AuthenticationRequest credentials, String username) {

        Trainee trainee = traineeRepository.findByUserUsername(username).orElseThrow(() -> {
            log.warn("Attempted to delete non-existing trainee username={}", username);
            return new  NoSuchElementException("Trainee with username " + username + " does not exist");
        });

        authenticationService.authenticateAndAuthorize(credentials.username(), credentials.password(), trainee.getUser().getUsername());

        trainingRepository.deleteAllByTraineeUserUsername(username);
        traineeRepository.delete(trainee);
        log.info("Trainee username={} and all associated trainings deleted", username);
    }

    @Override
    public Trainee selectTraineeProfile(@NotNull AuthenticationRequest credentials, Long id) {
        authenticationService.authenticate(credentials.username(), credentials.password());
        return traineeRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Trainee with id " + id + " does not exist"));
    }

    @Override
    public Trainee selectTraineeProfileByUsername(@NotNull AuthenticationRequest credentials, String username) {
        authenticationService.authenticate(credentials.username(), credentials.password());
        return traineeRepository.findByUserUsername(username).orElseThrow(() -> new NoSuchElementException("Trainee with username " + username + " does not exist"));
    }

    @Override
    public List<Trainer> selectUnassignedTrainers(AuthenticationRequest credentials, String username) {
        authenticationService.authenticate(credentials.username(), credentials.password());
        return trainerRepository.findUnassignedTrainers(username);
    }

    @Override
    @Transactional
    public List<Trainer> updateTraineeTrainers(AuthenticationRequest credentials, String username, List<String> usernames) {
        authenticationService.authenticateAndAuthorize(credentials.username(), credentials.password(), username);
        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Trainee with username " + username + " does not exist"));

        List<Trainer> trainers = trainerRepository.findAllByUserUsernameIn(usernames);
        if (trainers.size() != usernames.size()) {
            throw new NoSuchElementException("One or more trainer IDs do not exist");
        }

        trainee.setTrainers(trainers);
        traineeRepository.save(trainee);
        log.info("Trainee username={} trainer list updated to {} trainer(s)", username, trainers.size());
        return trainers;
    }
}
