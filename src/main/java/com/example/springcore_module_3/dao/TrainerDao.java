package com.example.springcore_module_3.dao;

import com.example.springcore_module_3.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerDao {

    Trainer create(Trainer trainer);
    boolean update(Trainer trainer);
    Optional<Trainer> delete(Long id);
    Optional<Trainer> findById(Long id);
    Optional<Trainer> findByUsername(String username);
    List<Trainer> findAll();
}
