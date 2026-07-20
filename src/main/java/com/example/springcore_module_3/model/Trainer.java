package com.example.springcore_module_3.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Trainer extends User {

    private TrainingType specialization;


    public Trainer(long userId, String firstName, String lastName, String username, String password, TrainingType specialization) {
        super(userId, firstName, lastName, username, password);
        this.specialization = specialization;
    }

}
