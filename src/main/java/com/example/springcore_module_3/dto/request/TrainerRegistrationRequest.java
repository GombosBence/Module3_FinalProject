package com.example.springcore_module_3.dto.request;

import com.example.springcore_module_3.dto.TrainingTypeDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrainerRegistrationRequest(@NotBlank String firstName, @NotBlank String lastName,
                                         @NotNull TrainingTypeDto specialization) {
}
