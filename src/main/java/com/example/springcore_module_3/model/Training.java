package com.example.springcore_module_3.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainingId;

    @ManyToOne
    @JoinColumn(name = "trainee_id", nullable = false)
    private Trainee trainee;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @Column(nullable = false)
    private String trainingName;

    @ManyToOne
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingType trainingType;

    @Column(nullable = false)
    private LocalDate trainingDate;

    @Column(nullable = false)
    private Duration trainingDuration;


    public Training(){}

    public Training(Trainee trainee, Trainer trainer, String trainingName, TrainingType trainingType, LocalDate trainingDate, Duration trainingDuration) {
        this.trainee = trainee;
        this.trainer = trainer;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }

}
