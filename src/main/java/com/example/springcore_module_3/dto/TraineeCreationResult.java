package com.example.springcore_module_3.dto;

import com.example.springcore_module_3.model.Trainee;

public record TraineeCreationResult(Trainee trainee, String rawPassword) {}
