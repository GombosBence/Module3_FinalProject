package com.example.springcore_module_3.mapper;

import com.example.springcore_module_3.dto.TrainingTypeDto;
import com.example.springcore_module_3.model.TrainingType;

public class TrainingTypeMapper {

    public static TrainingType fromDto(TrainingTypeDto dto) {
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeId(dto.id());
        trainingType.setTrainingTypeName(dto.name());
        return trainingType;
    }

    public static TrainingTypeDto toDto(TrainingType trainingType) {
        return new TrainingTypeDto(
                trainingType.getTrainingTypeId(),
                trainingType.getTrainingTypeName()
        );
    }

}
