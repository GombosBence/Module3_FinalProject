package com.example.springcore_module_3.dto;

import java.time.LocalDate;

public record TraineeProfileDto(String username, String firstName, String lastName, LocalDate dateOfBirth, String address, boolean isActive) {
}
