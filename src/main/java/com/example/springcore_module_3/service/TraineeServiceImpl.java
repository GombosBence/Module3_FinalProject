package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dto.AuthenticationRequestDto;
import com.example.springcore_module_3.dto.TraineeCreationResultDto;
import com.example.springcore_module_3.exception.InvalidStateTransitionException;
import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.User;
import com.example.springcore_module_3.repository.TraineeRepository;
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


    public TraineeServiceImpl(TraineeRepository traineeRepository, PasswordGenerator passwordGenerator,
                              UsernameGenerator usernameGenerator, PasswordEncoder passwordEncoder,
                              AuthenticationService authenticationService, TrainingRepository trainingRepository, UserRepository userRepository) {
        this.traineeRepository = traineeRepository;
        this.passwordGenerator = passwordGenerator;
        this.usernameGenerator = usernameGenerator;
        this.passwordEncoder = passwordEncoder;
        this.trainingRepository = trainingRepository;
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    @Override
    public TraineeCreationResultDto createTraineeProfile(User user, String address, LocalDate dateOfBirth) {
        String password = passwordGenerator.generatePassword(10);
        String username = usernameGenerator.generateUsername(user.getFirstName(), user.getLastName(),
                userRepository::existsByUsername);

        String hashPassword = passwordEncoder.encode(password);
        user.setPassword(hashPassword);
        user.setUsername(username);
        Trainee newTrainee = new Trainee(user, address, dateOfBirth);
        traineeRepository.save(newTrainee);
        log.info("Trainee created with username: {}", username);
        return new TraineeCreationResultDto(newTrainee, password);
    }

    @Override
    @Transactional
    public void updateTraineeProfile(@NotNull AuthenticationRequestDto credentials, Trainee trainee) {

        Trainee original = traineeRepository.findByUserUsername(trainee.getUser().getUsername()).orElseThrow(() ->{
            log.warn("Trainee not found with username: {}", trainee.getUser().getUsername());
            return new NoSuchElementException("Trainee not found");});

        authenticationService.authenticateAndAuthorize(credentials.username(), credentials.password(), trainee.getUser().getUsername());

        if(!original.getUser().getFirstName().equals(trainee.getUser().getFirstName())
            || !original.getUser().getLastName().equals(trainee.getUser().getLastName())) {
            log.info("Trainee last/first name changed -> creating new username");
            String newUsername = usernameGenerator.generateUsername(trainee.getUser().getFirstName(), trainee.getUser().getLastName(),
                    userRepository::existsByUsername);
            original.getUser().setFirstName(trainee.getUser().getFirstName());
            original.getUser().setLastName(trainee.getUser().getLastName());
            original.getUser().setUsername(newUsername);
        }
        if(trainee.getAddress() != null) original.setAddress(trainee.getAddress());
        if(trainee.getDateOfBirth() != null) original.setDateOfBirth(trainee.getDateOfBirth());

        traineeRepository.save(original);
    }

    @Override
    @Transactional
    public void deactivateTraineeProfile(@NotNull AuthenticationRequestDto credentials, Long id) {

        authenticationService.authenticate(credentials.username(), credentials.password());
        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Attempted to deactivate non-existing trainee id={}", id);
                    return new NoSuchElementException("Trainee with id " + id + " does not exist");
                });

        authenticationService.authenticateAndAuthorize(credentials.username(), credentials.password(), trainee.getUser().getUsername());

        if(!trainee.getUser().isActive()) {
            log.warn("Attempted to deactivate already inactive trainee id={}", id);
            throw new InvalidStateTransitionException("Trainee already inactive " + trainee.getUser().getUsername());
        }

        trainee.getUser().setActive(false);;
        traineeRepository.save(trainee);
        log.info("Trainee id={} deactivated", id);
    }

    @Override
    @Transactional
    public void activateTraineeProfile(@NotNull AuthenticationRequestDto credentials, Long id) {

        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Attempted to activate non-existing trainee id={}", id);
                    return new NoSuchElementException("Trainee with id " + id + " does not exist");
                });

        authenticationService.authenticateAndAuthorize(credentials.username(), credentials.password(), trainee.getUser().getUsername());

        if(trainee.getUser().isActive()) {
            log.warn("Attempted to activate already active trainee id={}", id);
            throw new InvalidStateTransitionException("Trainee already active " + trainee.getUser().getUsername());
        }

        trainee.getUser().setActive(true);
        traineeRepository.save(trainee);
        log.info("Trainee id={} activated", id);
    }

    @Override
    @Transactional
    public void deleteTraineeProfile(@NotNull AuthenticationRequestDto credentials, String username) {

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
    public Trainee selectTraineeProfile(@NotNull AuthenticationRequestDto credentials, Long id) {
        authenticationService.authenticate(credentials.username(), credentials.password());
        return traineeRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Trainee with id " + id + " does not exist"));
    }

    @Override
    public Trainee selectTraineeProfileByUsername(@NotNull AuthenticationRequestDto credentials, String username) {
        authenticationService.authenticate(credentials.username(), credentials.password());
        return traineeRepository.findByUserUsername(username).orElseThrow(() -> new NoSuchElementException("Trainee with username " + username + " does not exist"));
    }
}
