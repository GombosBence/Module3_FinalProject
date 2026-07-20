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
        log.info("Trainee Storage initialized with {} records, next id is {}", traineeStorage.size(),  maxId + 1);
    }

    @Override
    public Trainee create(Trainee trainee) {
        Long id = counter.incrementAndGet();
        trainee.setUserId(id);
        traineeStorage.put(id, trainee);
        log.info("Trainee created with id {}", id);
        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        Long id = trainee.getUserId();
        if(traineeStorage.containsKey(id)){
            traineeStorage.put(id, trainee);
            log.info("Trainee updated with id {}", id);
            return trainee;
        }else{
            log.warn("Attempted to update non-existing trainee id={}", id);
            throw new IllegalStateException("Trainee not found");
        }
    }

    @Override
    public void delete(Long id) {
        if(traineeStorage.containsKey(id)){
            traineeStorage.remove(id);
            log.info("Trainee deleted with id {}", id);
        }else{
            log.warn("Attempted to delete non-existing trainee id={}", id);
            throw new IllegalStateException("Trainee not found");
        }
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
