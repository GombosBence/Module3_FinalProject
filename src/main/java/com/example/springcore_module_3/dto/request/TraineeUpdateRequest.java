package com.example.springcore_module_3.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TraineeUpdateRequest (@NotBlank String firstName,@NotBlank String lastName, LocalDate dateOfBirth, String address,@NotNull Boolean isActive){
}
