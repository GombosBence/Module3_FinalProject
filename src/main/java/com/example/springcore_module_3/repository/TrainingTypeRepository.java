package com.example.springcore_module_3.repository;

import com.example.springcore_module_3.model.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long>
{
}

