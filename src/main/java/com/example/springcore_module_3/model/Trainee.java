package com.example.springcore_module_3.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Trainee{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long traineeId;

    @Column
    private String address;

    @Column
    private LocalDate dateOfBirth;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToMany
            @JoinTable(name = "trainee_trainer",
                    joinColumns = @JoinColumn(name = "trainee_id"),
                    inverseJoinColumns = @JoinColumn(name = "trainer_id"))
    private List<Trainer> trainers = new ArrayList<>();

    public Trainee(){}

    public Trainee(User user, String address, LocalDate dateOfBirth) {
        this.user = user;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }


}
