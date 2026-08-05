package com.example.springcore_module_3.mapper;

import com.example.springcore_module_3.dto.TrainingTypeDto;
import com.example.springcore_module_3.model.TrainingType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainingTypeMapperTest {

    @Test
    void fromDto_buildsCorrectEntity() {
        TrainingTypeDto dto = new TrainingTypeDto(1L, "FITNESS");

        TrainingType result = TrainingTypeMapper.fromDto(dto);

        assertEquals(1L, result.getTrainingTypeId());
        assertEquals("FITNESS", result.getTrainingTypeName());
    }

    @Test
    void toDto_buildsCorrectDto() {
        TrainingType type = new TrainingType("YOGA");
        type.setTrainingTypeId(2L);

        TrainingTypeDto dto = TrainingTypeMapper.toDto(type);

        assertEquals(2L, dto.id());
        assertEquals("YOGA", dto.name());
    }
}