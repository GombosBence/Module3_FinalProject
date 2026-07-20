package com.example.springcore_module_3.dao;

import com.example.springcore_module_3.model.Trainee;
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
public class TraineeDaoImpl implements TraineeDao{

    private final AtomicLong counter = new AtomicLong(0);
    private Map<Long, Trainee> traineeStorage;

    @Autowired
    @Qualifier("traineeStorage")
    public void setStorage(Map<Long, Trainee> traineeStorage){
        this.traineeStorage = traineeStorage;
    }

    @PostConstruct
    public void init(){
        long maxId = traineeStorage.keySet().stream().mapToLong(Long::longValue).max().orElse(0);
        counter.set(maxId);
        log.debug("Trainee Storage initialized with {} records, next id is {}", traineeStorage.size(),  maxId + 1);
    }

    @Override
    public Trainee create(Trainee trainee) {
        Long id = counter.incrementAndGet();
        trainee.setUserId(id);
        traineeStorage.put(id, trainee);
        log.debug("Trainee inserted with id {}", id);
        return trainee;
    }

    @Override
    public boolean update(Trainee trainee) {
       if(!traineeStorage.containsKey(trainee.getUserId())){
           return false;
       }
       traineeStorage.put(trainee.getUserId(), trainee);
       log.debug("Trainee updated with id {}", trainee.getUserId());
       return true;
    }

    @Override
    public Optional<Trainee> delete(Long id) {
        Optional<Trainee> deletedTrainee = Optional.ofNullable(traineeStorage.remove(id));

        if (deletedTrainee.isPresent()) {
            log.debug("Trainee removed from storage with id {}", id);
        } else {
            log.debug("No trainee found in storage with id {}", id);
        }
        return deletedTrainee;
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(traineeStorage.get(id));
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        return traineeStorage.values().stream()
                .filter(trainee -> trainee.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public List<Trainee> findAll() {
        return traineeStorage.values().stream().toList();
    }
}
