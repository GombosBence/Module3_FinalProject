package com.example.springcore_module_3.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Trainee extends User{

    private String address;
    private LocalDate dateOfBirth;


    public Trainee(){}

    public Trainee(String firstName, String lastName, String username, String password, String address, LocalDate dateOfBirth) {
        super(firstName, lastName, username, password);
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }


}
