package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dao.TraineeDao;
import com.example.springcore_module_3.dao.TrainerDao;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.TrainingType;
import com.example.springcore_module_3.util.PasswordGenerator;
import com.example.springcore_module_3.util.UsernameGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
public class TrainerServiceImpl implements TrainerService {

    private TrainerDao trainerDao;
    private TraineeDao traineeDao;
    private PasswordGenerator passwordGenerator;
    private UsernameGenerator usernameGenerator;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Trainer createTrainerProfile(String firstName, String lastName, TrainingType trainingType) {
        String password = passwordGenerator.generatePassword(10);
        String username = usernameGenerator.generateUsername(firstName, lastName,
                (name) -> trainerDao.findByUsername(name).isPresent()
                                || traineeDao.findByUsername(name).isPresent());

        password = passwordEncoder.encode(password);
        Trainer newTrainer = new Trainer(firstName, lastName, username, password, trainingType);
        trainerDao.create(newTrainer);
        log.info("Trainer created with username: {}", newTrainer.getUsername());
        return newTrainer;
    }

    @Override
    public void updateTrainerProfile(Trainer trainer) {
        boolean result = trainerDao.update(trainer);
        if(!result){
            log.warn("Attempted to update a non-existing trainer id={}", trainer.getUserId());
            throw new NoSuchElementException("Trainer not found");
        }
    }

    @Override
    public Trainer selectTrainerProfile(Long id) {
        return trainerDao.findById(id).orElseThrow(() -> new NoSuchElementException("Trainer with id " + id + " does not exist"));
    }

    @Override
    public Trainer selectTrainerProfileByUsername(String username) {
        return trainerDao.findByUsername(username).orElseThrow(() -> new NoSuchElementException("Trainer with username " + username + " does not exist"));
    }
}
