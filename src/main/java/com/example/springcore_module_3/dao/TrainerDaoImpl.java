package com.example.springcore_module_3.dao;

import com.example.springcore_module_3.model.Trainer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Repository
public class TrainerDaoImpl implements TrainerDao {

    private final AtomicLong counter = new AtomicLong(0);
    private Map<Long, Trainer> trainerStorage;

    @Autowired
    @Qualifier("trainerStorage")
    public void setStorage(Map<Long, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @PostConstruct
    public void init(){
        long maxId = trainerStorage.keySet().stream().mapToLong(Long::longValue).max().orElse(0);
        counter.set(maxId);
        log.debug("Trainer Storage initialized with {} records, next id is {}", trainerStorage.size(),  maxId + 1);
    }

    @Override
    public Trainer create(Trainer trainer) {
        long id = counter.incrementAndGet();
        trainer.setUserId(id);
        trainerStorage.put(id, trainer);
        log.debug("Trainer created with id {}", id);
        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        long id = trainer.getUserId();
        if(trainerStorage.containsKey(id)){
            trainerStorage.put(id, trainer);
            log.debug("Trainer updated with id {}", id);
            return trainer;
        }else{
            log.warn("Attempted to update non-existing trainer id={}", id);
            throw new IllegalStateException("Trainer not found");
        }
    }

    @Override
    public void delete(Long id) {
        if(trainerStorage.containsKey(id)){
            trainerStorage.remove(id);
            log.debug("Trainer deleted with id {}", id);
        }else {
            log.warn("Attempted to delete non-existing trainer id={}", id);
            throw new IllegalStateException("Trainer not found");
        }
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(trainerStorage.get(id));
    }
}
