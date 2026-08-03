package com.example.springcore_module_3.controller;


import com.example.springcore_module_3.dto.TraineeCreationResult;
import com.example.springcore_module_3.dto.TrainerProfileDto;
import com.example.springcore_module_3.dto.request.*;
import com.example.springcore_module_3.dto.response.TraineeGetProfileResponse;
import com.example.springcore_module_3.dto.response.TraineeRegistrationResponse;
import com.example.springcore_module_3.dto.response.TraineeUpdateResponse;
import com.example.springcore_module_3.facade.GymFacade;
import com.example.springcore_module_3.mapper.TraineeMapper;
import com.example.springcore_module_3.mapper.TrainerMapper;
import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.Trainer;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/trainee")
public class TraineeController {

    private final GymFacade  gymFacade;

    public TraineeController(GymFacade gymFacade){
        this.gymFacade = gymFacade;
    }

    @PostMapping
    public ResponseEntity<TraineeRegistrationResponse> register(@RequestBody @Valid TraineeRegistrationRequest request){

        TraineeCreationResult result  = gymFacade.createTrainee(request.firstname(), request.lastname(),
                request.address(), request.dateOfBirth());

        TraineeRegistrationResponse response = new TraineeRegistrationResponse(result.trainee().getUser().getUsername(), result.rawPassword());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TraineeGetProfileResponse> getTrainee(
            @PathVariable String username,
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword)
    {

        AuthenticationRequest credentials = new AuthenticationRequest(authUsername, authPassword);
        Trainee trainee = gymFacade.getTraineeByUsername(credentials, username);

        return ResponseEntity.ok(TraineeMapper.toProfileResponse(trainee));
    }

    @PutMapping("/{username}")
    public ResponseEntity<TraineeUpdateResponse> updateTrainee(@PathVariable String username,
                                                               @RequestHeader("X-Username") String authUsername,
                                                               @RequestHeader("X-Password") String authPassword,
                                                               @RequestBody @Valid TraineeUpdateRequest request)
    {

        AuthenticationRequest credentials = new AuthenticationRequest(authUsername, authPassword);
        Trainee trainee = gymFacade.updateTrainee(credentials, TraineeMapper.toTrainee(username, request));

        return ResponseEntity.ok(TraineeMapper.toUpdateResponse(trainee));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainee(@PathVariable String username,
                                              @RequestHeader("X-Username") String authUsername,
                                              @RequestHeader("X-Password") String authPassword)
    {
        AuthenticationRequest credentials = new AuthenticationRequest(authUsername, authPassword);
        gymFacade.deleteTrainee(credentials, username);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/unassigned-trainers")
    public ResponseEntity<List<TrainerProfileDto>> getTraineeUnassignedTrainers(@PathVariable String username,
                                                                                @RequestHeader("X-Username") String authUsername,
                                                                                @RequestHeader("X-Password") String authPassword)
    {
        AuthenticationRequest credentials = new AuthenticationRequest(authUsername, authPassword);
        List<Trainer> result = gymFacade.getUnassignedTrainers(credentials, username);

        List<TrainerProfileDto> profiles = result.stream().map(TrainerMapper::toProfileDto).toList();

        return ResponseEntity.ok(profiles);
    }


    @PutMapping("/{username}/trainers")
    public ResponseEntity<List<TrainerProfileDto>> updateTraineeTrainerList(@PathVariable String username,
                                                                            @RequestHeader("X-Username") String authUsername,
                                                                            @RequestHeader("X-Password") String authPassword,
                                                                            @RequestBody @Valid UpdateTraineeTrainersRequest request)
    {
        AuthenticationRequest credentials = new AuthenticationRequest(authUsername, authPassword);
        List<Trainer> result = gymFacade.updateTraineeTrainers(credentials, username, request.trainerUsernames());

        List<TrainerProfileDto> profiles = result.stream().map(TrainerMapper::toProfileDto).toList();

        return ResponseEntity.ok(profiles);
    }

    @PatchMapping("/status")
    public ResponseEntity<Void> setTraineeActiveStatus(
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword,
            @RequestBody @Valid TraineeSetActivateRequest request) {

        AuthenticationRequest credentials = new AuthenticationRequest(authUsername, authPassword);

        if (request.isActive()) {
            gymFacade.activateTrainee(credentials, request.username());
        } else {
            gymFacade.deactivateTrainee(credentials, request.username());
        }

        return ResponseEntity.ok().build();
    }


}
