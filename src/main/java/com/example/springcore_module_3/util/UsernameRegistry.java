package com.example.springcore_module_3.util;


import com.example.springcore_module_3.dao.TraineeDao;
import com.example.springcore_module_3.dao.TrainerDao;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class UsernameRegistry {

    private final Set<String> takenUsernames = ConcurrentHashMap.newKeySet();

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @PostConstruct
    public void seedFromStorage() {
        traineeDao.findAll().forEach(t -> takenUsernames.add(t.getUsername()));
        trainerDao.findAll().forEach(t -> takenUsernames.add(t.getUsername()));
        log.info("UsernameRegistry seeded with {} existing username(s)", takenUsernames.size());
    }

    public boolean tryReserve(String username) {
        return takenUsernames.add(username);
    }
}
