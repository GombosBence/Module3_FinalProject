package com.example.springcore_module_3.mapper;

import com.example.springcore_module_3.dto.TraineeProfileDto;
import com.example.springcore_module_3.dto.TrainerProfileDto;
import com.example.springcore_module_3.dto.request.TraineeUpdateRequest;
import com.example.springcore_module_3.dto.response.TraineeGetProfileResponse;
import com.example.springcore_module_3.dto.response.TraineeUpdateResponse;
import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.User;

import java.util.List;

public class TraineeMapper {

    public static TraineeGetProfileResponse toProfileResponse(Trainee trainee) {
        List<TrainerProfileDto> trainers = trainee.getTrainers().stream()
                .map(TrainerMapper::toProfileDto)
                .toList();

        return new TraineeGetProfileResponse(
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.getUser().isActive(),
                trainers
        );
    }

    public static Trainee toTrainee(String username,TraineeUpdateRequest request) {

        User user = new User(request.firstName(), request.lastName(), username, null, request.isActive());

        return new Trainee(
                user,
                request.address(),
                request.dateOfBirth()
        );
    }

    public static TraineeUpdateResponse toUpdateResponse(Trainee trainee) {

        List<TrainerProfileDto> trainers = trainee.getTrainers().stream().map(TrainerMapper::toProfileDto).toList();

        return new TraineeUpdateResponse(
                trainee.getUser().getUsername(),
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.getUser().isActive(),
                trainers
        );
    }

    public static TraineeProfileDto toProfileDto(Trainee trainee) {
        return new TraineeProfileDto(
                trainee.getUser().getUsername(),
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName()
        );
    }

}
