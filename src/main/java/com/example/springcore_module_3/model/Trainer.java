package com.example.springcore_module_3.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainerId;

    @ManyToOne
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingType specialization;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToMany(mappedBy = "trainers")
    private List<Trainee> trainees = new ArrayList<>();


    public Trainer(){}

    public Trainer(User user, TrainingType specialization) {
       this.user = user;
        this.specialization = specialization;
    }

}
