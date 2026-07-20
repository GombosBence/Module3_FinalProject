package com.example.springcore_module_3.dao;

import com.example.springcore_module_3.model.Trainee;

import java.util.Optional;

public interface TraineeDao {

    Trainee create(Trainee trainee);
    Trainee update(Trainee trainee);
    void delete(Long id);
    Optional<Trainee> findById(Long id);
}
