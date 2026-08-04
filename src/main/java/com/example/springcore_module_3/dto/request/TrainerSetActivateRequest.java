package com.example.springcore_module_3.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrainerSetActivateRequest(@NotBlank String username, @NotNull Boolean isActive) {
}
