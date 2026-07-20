package com.example.springcore_module_3.service;

import com.example.springcore_module_3.model.Trainee;

import java.time.LocalDate;

public interface TraineeService {

    Trainee createTraineeProfile(String firstName, String lastName, String address, LocalDate dateOfBirth);
    void updateTraineeProfile(Trainee trainee);
    void deleteTraineeProfile(Long id);
    Trainee selectTraineeProfile(Long id);
    Trainee selectTraineeProfileByUsername(String username);

}
