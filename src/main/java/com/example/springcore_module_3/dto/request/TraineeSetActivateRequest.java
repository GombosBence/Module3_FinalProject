package com.example.springcore_module_3.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TraineeSetActivateRequest(@NotBlank String username, @NotNull Boolean isActive) {
}
