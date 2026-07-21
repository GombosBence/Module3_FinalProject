package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dao.TraineeDao;
import com.example.springcore_module_3.dao.TrainerDao;
import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.util.PasswordGenerator;
import com.example.springcore_module_3.util.UsernameGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
public class TraineeServiceImpl implements TraineeService {

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private PasswordGenerator passwordGenerator;
    private UsernameGenerator usernameGenerator;

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

    @Override
    public Trainee createTraineeProfile(String firstName, String lastName, String address, LocalDate dateOfBirth) {
        String password = passwordGenerator.generatePassword(10);
        String username = usernameGenerator.generateUsername(firstName, lastName,
                (name) -> traineeDao.findByUsername(name).isPresent()
                                || trainerDao.findByUsername(name).isPresent());

        Trainee newTrainee = new Trainee(firstName, lastName, username, password, address, dateOfBirth);
        traineeDao.create(newTrainee);
        log.info("Trainee created with username: {}", newTrainee.getUsername());
        return newTrainee;
    }

    @Override
    public void updateTraineeProfile(Trainee trainee) {
        boolean result = traineeDao.update(trainee);
        if(!result){
            log.warn("Attempted to update non-existing trainee id={}", trainee.getUserId());
            throw new NoSuchElementException("Trainee not found");
        }
    }

    @Override
    public void deleteTraineeProfile(Long id) {
        Optional<Trainee> traineeOptional = traineeDao.delete(id);
        if(traineeOptional.isEmpty()){
            log.warn("Attempted to delete non-existing trainee id={}", id);
            throw new NoSuchElementException("Trainee not found");
        }
    }

    @Override
    public Trainee selectTraineeProfile(Long id) {
        return traineeDao.findById(id).orElseThrow(() -> new NoSuchElementException("Trainee with id " + id + " does not exist"));
    }

    @Override
    public Trainee selectTraineeProfileByUsername(String username) {
        return traineeDao.findByUsername(username).orElseThrow(() -> new NoSuchElementException("Trainee with username " + username + " does not exist"));
    }
}
