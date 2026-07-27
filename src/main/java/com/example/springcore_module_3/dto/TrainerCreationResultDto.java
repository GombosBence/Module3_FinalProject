package com.example.springcore_module_3.dto;

import com.example.springcore_module_3.model.Trainer;

public record TrainerCreationResultDto(Trainer trainer, String rawPassword) {}
