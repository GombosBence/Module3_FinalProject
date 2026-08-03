package com.example.springcore_module_3.repository;

import com.example.springcore_module_3.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByUserUsername(String username);

    List<Trainer> findAllByUserUsernameIn(List<String> usernames);

    @Query("""
        SELECT tr FROM Trainer tr
        WHERE tr NOT IN (
            SELECT t FROM Trainee te JOIN te.trainers t WHERE te.user.username = :traineeUsername
        )
""")
    List<Trainer> findUnassignedTrainers(@Param("traineeUsername")  String traineeUsername);
}
