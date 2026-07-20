package com.example.springcore_module_3.dao;

import com.example.springcore_module_3.model.Training;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Repository
public class TrainingDaoImpl implements TrainingDao {

    private final AtomicLong counter = new AtomicLong(0);
    private Map<Long, Training> trainingStorage;

    @Autowired
    @Qualifier("trainingStorage")
    public void setStorage(Map<Long, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @PostConstruct
    public void init(){
        long maxId = trainingStorage.keySet().stream().mapToLong(Long::longValue).max().orElse(0);
        counter.set(maxId);
        log.info("Training Storage initialized with {} records, next id is {}", trainingStorage.size(),  maxId + 1);
    }

    @Override
    public Training create(Training training) {
        long id = counter.incrementAndGet();
        training.setTrainingId(id);
        trainingStorage.put(id, training);
        log.info("Training storage created with id {}", id);
        return training;
    }

    @Override
    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(trainingStorage.get(id));
    }

    @Override
    public List<Training> findAllByTrainee(Long traineeId) {
        return trainingStorage.values().stream()
                .filter(training -> training.getTraineeId().equals(traineeId))
                .toList();
    }

    @Override
    public List<Training> findAllByTrainer(Long trainerId) {
        return trainingStorage.values().stream()
                .filter(training -> training.getTrainerId().equals(trainerId))
                .toList();
    }
}
