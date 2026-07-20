package com.example.springcore_module_3.dao;

import com.example.springcore_module_3.model.Trainer;

import java.util.Optional;

public interface TrainerDao {

    Trainer create(Trainer trainer);
    Trainer update(Trainer trainer);
    void delete(Long id);
    Optional<Trainer> findById(Long id);
}
