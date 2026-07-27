package com.example.springcore_module_3.repository;

import com.example.springcore_module_3.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    Optional<Trainee> findByUserUsername(String username);

    void deleteByUserUsername(String username);

}
