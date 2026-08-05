package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dto.request.AuthenticationRequest;
import com.example.springcore_module_3.model.TrainingType;

import java.util.List;

public interface TrainingTypeService {

    List<TrainingType> findAll(AuthenticationRequest credentials);

}
