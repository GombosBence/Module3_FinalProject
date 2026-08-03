package com.example.springcore_module_3.mapper;

import com.example.springcore_module_3.dto.TrainerProfileDto;
import com.example.springcore_module_3.dto.TrainingTypeDto;
import com.example.springcore_module_3.model.Trainer;

public class TrainerMapper {


    public static TrainerProfileDto toProfileDto(Trainer trainer){
       return new TrainerProfileDto(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                new TrainingTypeDto(trainer.getSpecialization().getTrainingTypeId(), trainer.getSpecialization().getTrainingTypeName())
        );
    }


}
