package com.example.springcore_module_3.dao;

import com.example.springcore_module_3.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeDao {

    Trainee create(Trainee trainee);
    boolean update(Trainee trainee);
    Optional<Trainee> delete(Long id);
    Optional<Trainee> findById(Long id);
    Optional<Trainee> findByUsername(String username);
    List<Trainee> findAll();
}
