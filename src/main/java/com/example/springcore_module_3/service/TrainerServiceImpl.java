package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dto.request.AuthenticationRequest;
import com.example.springcore_module_3.dto.TrainerCreationResult;
import com.example.springcore_module_3.exception.InvalidStateTransitionException;
import com.example.springcore_module_3.metrics.GymMetrics;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.TrainingType;
import com.example.springcore_module_3.model.User;
import com.example.springcore_module_3.repository.TrainerRepository;
import com.example.springcore_module_3.repository.UserRepository;
import com.example.springcore_module_3.util.PasswordGenerator;
import com.example.springcore_module_3.util.UsernameGenerator;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;
    private final GymMetrics gymMetrics;

    public TrainerServiceImpl(TrainerRepository trainerRepository, PasswordGenerator passwordGenerator,
                              UsernameGenerator usernameGenerator, PasswordEncoder encoder,
                              UserRepository userRepository, AuthenticationService authenticationService, GymMetrics gymMetrics) {
        this.trainerRepository = trainerRepository;
        this.passwordGenerator = passwordGenerator;
        this.usernameGenerator = usernameGenerator;
        this.passwordEncoder = encoder;
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.gymMetrics = gymMetrics;
    }


    @Override
    public TrainerCreationResult createTrainerProfile(User user, TrainingType trainingType) {
        String password = passwordGenerator.generatePassword(10);
        String username = usernameGenerator.generateUsername(user.getFirstName(), user.getLastName(),
                userRepository::existsByUsername);

        String hashPassword = passwordEncoder.encode(password);
        user.setPassword(hashPassword);
        user.setUsername(username);
        Trainer newTrainer = new Trainer(user, trainingType);
        trainerRepository.save(newTrainer);
        log.info("Trainer created with username: {}", username);
        gymMetrics.incrementTrainerRegistrations();
        return new TrainerCreationResult(newTrainer, password);
    }

    @Override
    @Transactional
    public Trainer updateTrainerProfile(@NotNull AuthenticationRequest credentials, Trainer trainer) {

        Trainer original = trainerRepository.findByUserUsername(trainer.getUser().getUsername()).orElseThrow(() -> {
            log.warn("Trainer not found with username: {}", trainer.getUser().getUsername());
            return new NoSuchElementException("Trainer not found with username: " + trainer.getUser().getUsername());
        });

        authenticationService.authenticateAndAuthorize(credentials.username(), credentials.password(), trainer.getUser().getUsername());

        if(trainer.getUser().getFirstName() != null) original.getUser().setFirstName(trainer.getUser().getFirstName());
        if(trainer.getUser().getLastName() != null) original.getUser().setLastName(trainer.getUser().getLastName());
        if(trainer.getSpecialization() != null) original.setSpecialization(trainer.getSpecialization());
        original.getUser().setActive(trainer.getUser().isActive());

        trainerRepository.save(original);
        return original;
    }

    @Override
    @Transactional
    public void deactivateTrainerProfile(@NotNull AuthenticationRequest credentials, @NotNull String username) {

        Trainer trainer = trainerRepository.findByUserUsername(username).orElseThrow(() -> {
            log.warn("Trainer not found with username : {}", username);
            return new NoSuchElementException("Trainer not found with username: " + username);
        });

        authenticationService.authenticateAndAuthorize(credentials.username(), credentials.password(), trainer.getUser().getUsername());

        if(!trainer.getUser().isActive()){
            log.warn("Attempted to deactivate an already inactive Trainer");
            throw new InvalidStateTransitionException("Attempted to deactivate an already inactive Trainer");
        }

        trainer.getUser().setActive(false);
        trainerRepository.save(trainer);
        log.info("Trainer deactivated with username: {}", trainer.getUser().getUsername());
    }

    @Override
    @Transactional
    public void activateTrainerProfile(@NotNull AuthenticationRequest credentials, @NotNull String username) {

        Trainer trainer = trainerRepository.findByUserUsername(username).orElseThrow(() -> {
            log.warn("Trainer not found with id : {}", username);
            return new NoSuchElementException("Trainer not found with id: " + username);
        });

        authenticationService.authenticateAndAuthorize(credentials.username(), credentials.password(), trainer.getUser().getUsername());

        if(trainer.getUser().isActive()){
            log.warn("Attempted to activate an already active Trainer");
            throw new InvalidStateTransitionException("Attempted to activate an already active Trainer");
        }

        trainer.getUser().setActive(true);
        trainerRepository.save(trainer);
        log.info("Trainer activated with username: {}", trainer.getUser().getUsername());
    }

    @Override
    public Trainer selectTrainerProfile(@NotNull AuthenticationRequest credentials, Long id) {
        authenticationService.authenticate(credentials.username(), credentials.password());
        return trainerRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Trainer with id " + id + " does not exist"));
    }

    @Override
    public Trainer selectTrainerProfileByUsername(@NotNull AuthenticationRequest credentials, String username) {
        authenticationService.authenticate(credentials.username(), credentials.password());
        return trainerRepository.findByUserUsername(username).orElseThrow(() -> new NoSuchElementException("Trainer with username " + username + " does not exist"));
    }
}
