package com.example.springcore_module_3.dto.response;

import com.example.springcore_module_3.dto.TraineeProfileDto;
import com.example.springcore_module_3.dto.TrainingTypeDto;

import java.util.List;

public record TrainerGetProfileResponse(String firstName, String lastName, TrainingTypeDto specialization,
                                        Boolean isActive, List<TraineeProfileDto> trainees) {
}
