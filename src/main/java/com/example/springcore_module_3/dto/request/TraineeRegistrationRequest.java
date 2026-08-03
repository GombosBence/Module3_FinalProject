package com.example.springcore_module_3.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record TraineeRegistrationRequest(@NotBlank String firstname, @NotBlank String lastname, String address, LocalDate dateOfBirth) {}
