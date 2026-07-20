package com.example.springcore_module_3.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Trainee extends User{

    private String address;
    private LocalDate dateOfBirth;

}
