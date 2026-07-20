package com.example.springcore_module_3.dao;

import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.Trainer;
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
        Long id = counter.incrementAndGet();
        trainer.setUserId(id);
        trainerStorage.put(id, trainer);
        log.info("Trainer inserted with id {}", id);
        return trainer;
    }

    @Override
    public boolean update(Trainer trainer) {
        if(!trainerStorage.containsKey(trainer.getUserId())){
            return false;
        }
        trainerStorage.put(trainer.getUserId(),trainer);
        log.debug("Trainer updated with id {}", trainer.getUserId());
        return true;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(trainerStorage.get(id));
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        return trainerStorage.values().stream()
                .filter(trainer -> trainer.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public List<Trainer> findAll() {
        return trainerStorage.values().stream().toList();
    }
}
