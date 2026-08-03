package com.example.springcore_module_3.dto.response;

import com.example.springcore_module_3.dto.TrainerProfileDto;

import java.time.LocalDate;
import java.util.List;

public record TraineeGetProfileResponse(String firstName, String lastName, LocalDate dateOfBirth, String address, boolean isActive,
                                       List<TrainerProfileDto> trainerProfiles) {}
