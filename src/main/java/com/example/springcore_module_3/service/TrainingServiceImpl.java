package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dao.TraineeDao;
import com.example.springcore_module_3.dao.TrainerDao;
import com.example.springcore_module_3.dao.TrainingDao;
import com.example.springcore_module_3.model.Training;
import com.example.springcore_module_3.model.TrainingType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
public class TrainingServiceImpl implements TrainingService {

    private TrainingDao trainingDao;
    private TrainerDao trainerDao;
    private TraineeDao traineeDao;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    public Training createTraining(Long traineeId, Long trainerId, String trainingName, TrainingType trainingType, LocalDate trainingDate, Duration trainingDuration) {
        if(traineeDao.findById(traineeId).isEmpty()){
            log.warn("Trainee with id {} not found", traineeId);
            throw new NoSuchElementException("Trainee with id " + traineeId + " not found");
        }
        if(trainerDao.findById(trainerId).isEmpty()){
            log.warn("Trainer with id {} not found", trainerId);
            throw new NoSuchElementException("Trainer with id " + trainerId + " not found");
        }
        Training newTraining  = new Training(traineeId, trainerId, trainingName, trainingType, trainingDate, trainingDuration);
        trainingDao.create(newTraining);
        log.info("Training has been created with name={}", trainingName);
        return newTraining;
    }

    @Override
    public Training getTrainingById(Long id) {
        Optional<Training> trainingOptional = trainingDao.findById(id);
        if(trainingOptional.isEmpty()){
            log.warn("Training with id {} not found", id);
            throw new NoSuchElementException("Training with id " + id + " not found");
        }
        return trainingOptional.get();
    }

    @Override
    public List<Training> selectAllTrainingsByTrainee(Long id) {
        return trainingDao.findAllByTrainee(id);
    }

    @Override
    public List<Training> selectAllTrainingsByTrainer(Long id) {
        return trainingDao.findAllByTrainer(id);
    }
}
