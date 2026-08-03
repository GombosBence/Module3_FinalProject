package com.example.springcore_module_3.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserPasswordChangeRequest(@NotBlank String username,@NotBlank String oldPassword,@NotBlank String newPassword) {
}
