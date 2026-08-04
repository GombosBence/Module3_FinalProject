package com.example.springcore_module_3.controller;


import com.example.springcore_module_3.dto.TraineeTrainingDto;
import com.example.springcore_module_3.dto.TrainerTrainingDto;
import com.example.springcore_module_3.dto.request.AuthenticationRequest;
import com.example.springcore_module_3.dto.request.TrainingCreationRequest;
import com.example.springcore_module_3.facade.GymFacade;
import com.example.springcore_module_3.mapper.TrainingMapper;
import com.example.springcore_module_3.model.Training;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/training")
public class TrainingController {

    private final GymFacade gymFacade;

    public TrainingController(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }


    @PostMapping
    public ResponseEntity<Void> addTraining(@RequestHeader("X-Username") String authUsername,
                                            @RequestHeader("X-Password") String authPassword,
                                            @RequestBody @Valid TrainingCreationRequest request)
    {
        AuthenticationRequest credentials = new AuthenticationRequest(authUsername, authPassword);
        gymFacade.createTraining(credentials, request.traineeUsername(), request.trainerUsername(), request.trainingName(),
                request.trainingDate(), request.trainingDuration());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/trainee/{username}/trainings")
    public ResponseEntity<List<TraineeTrainingDto>> getTraineeTraining(@PathVariable("username") String username,
                                                                       @RequestHeader("X-Username") String authUsername,
                                                                       @RequestHeader("X-Password") String authPassword,
                                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
                                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
                                                                       @RequestParam(required = false) String trainerName,
                                                                       @RequestParam(required = false) String trainingType)
    {
        AuthenticationRequest credentials = new AuthenticationRequest(authUsername, authPassword);
        List<Training> result = gymFacade.getTraineeTrainings(credentials, username, periodFrom, periodTo, trainerName, trainingType);

        return ResponseEntity.ok(TrainingMapper.mapToTraineeTrainingResponse(result));
    }

    @GetMapping("/trainer/{username}/trainings")
    public ResponseEntity<List<TrainerTrainingDto>> getTrainerTraining(@PathVariable("username") String username,
                                                                       @RequestHeader("X-Username") String authUsername,
                                                                       @RequestHeader("X-Password") String authPassword,
                                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
                                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
                                                                       @RequestParam(required = false) String traineeName)
    {
        AuthenticationRequest credentials = new AuthenticationRequest(authUsername, authPassword);
        List<Training> result = gymFacade.getTrainerTrainings(credentials, username, periodFrom, periodTo, traineeName);

        return ResponseEntity.ok(TrainingMapper.mapToTrainerTrainingResponse(result));
    }



}
