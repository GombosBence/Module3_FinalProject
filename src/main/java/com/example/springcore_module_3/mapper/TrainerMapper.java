package com.example.springcore_module_3.mapper;

import com.example.springcore_module_3.dto.TraineeProfileDto;
import com.example.springcore_module_3.dto.TrainerProfileDto;
import com.example.springcore_module_3.dto.TrainingTypeDto;
import com.example.springcore_module_3.dto.request.TrainerUpdateProfileRequest;
import com.example.springcore_module_3.dto.response.TrainerGetProfileResponse;
import com.example.springcore_module_3.dto.response.TrainerUpdateResponse;
import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.TrainingType;
import com.example.springcore_module_3.model.User;

import java.util.List;

public class TrainerMapper {


    public static TrainerProfileDto toProfileDto(Trainer trainer){
       return new TrainerProfileDto(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                new TrainingTypeDto(trainer.getSpecialization().getTrainingTypeId(), trainer.getSpecialization().getTrainingTypeName())
        );
    }


    public static TrainerGetProfileResponse toGetProfileResponse(Trainer trainer){

        List<TraineeProfileDto> trainees = trainer.getTrainees().stream().map(TraineeMapper::toProfileDto).toList();

        return new TrainerGetProfileResponse(
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                TrainingTypeMapper.toDto(trainer.getSpecialization()),
                trainer.getUser().isActive(),
                trainees
        );
    }

    public static TrainerUpdateResponse toUpdateResponse(Trainer trainer){

        List<TraineeProfileDto> trainees = trainer.getTrainees().stream().map(TraineeMapper::toProfileDto).toList();

        return new TrainerUpdateResponse(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                TrainingTypeMapper.toDto(trainer.getSpecialization()),
                trainer.getUser().isActive(),
                trainees
        );
    }

    public static Trainer toTrainer(String username, TrainerUpdateProfileRequest request){

        User user = new User(request.firstName(), request.lastName(), username, null);
        return new Trainer(
                user,
                TrainingTypeMapper.fromDto(request.trainingTypeDto())
        );
    }


}
