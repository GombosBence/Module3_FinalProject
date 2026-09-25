package com.example.springcore_module_3.controller;


import com.example.springcore_module_3.dto.TraineeTrainingDto;
import com.example.springcore_module_3.dto.TrainerTrainingDto;
import com.example.springcore_module_3.dto.request.AuthenticationRequest;
import com.example.springcore_module_3.dto.request.TrainingCreationRequest;
import com.example.springcore_module_3.facade.GymFacade;
import com.example.springcore_module_3.mapper.TrainingMapper;
import com.example.springcore_module_3.model.Training;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Tag(name = "Training", description = "Training creation and query")
@RestController
@RequestMapping("/api/training")
public class TrainingController {

    private final GymFacade gymFacade;

    public TrainingController(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }


    @Operation(summary = "Create training", description = "Creates a training")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Authentication failed, invalid credentials"),
            @ApiResponse(responseCode = "404", description = "Requested trainee/trainer not found")
    })
    @PostMapping
    public ResponseEntity<Void> addTraining(@RequestBody @Valid TrainingCreationRequest request)
    {
        gymFacade.createTraining(request.traineeUsername(), request.trainerUsername(), request.trainingName(),
                request.trainingDate(), request.trainingDuration());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get trainee's trainings", description = "Returns a list of trainings for trainee based on the query params")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful query"),
            @ApiResponse(responseCode = "401", description = "Authentication failed, invalid credentials")
    })
    @GetMapping("/trainee/{username}/trainings")
    public ResponseEntity<List<TraineeTrainingDto>> getTraineeTraining(@PathVariable("username") String username,
                                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
                                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
                                                                       @RequestParam(required = false) String trainerName,
                                                                       @RequestParam(required = false) String trainingType)
    {
        List<Training> result = gymFacade.getTraineeTrainings(username, periodFrom, periodTo, trainerName, trainingType);

        return ResponseEntity.ok(TrainingMapper.mapToTraineeTrainingResponse(result));
    }

    @Operation(summary = "Get trainer's trainings", description = "Returns a list of trainings for trainer based on the query params")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful query"),
            @ApiResponse(responseCode = "401", description = "Authentication failed, invalid credentials")
    })
    @GetMapping("/trainer/{username}/trainings")
    public ResponseEntity<List<TrainerTrainingDto>> getTrainerTraining(@PathVariable("username") String username,
                                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
                                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
                                                                       @RequestParam(required = false) String traineeName)
    {
        List<Training> result = gymFacade.getTrainerTrainings(username, periodFrom, periodTo, traineeName);
        return ResponseEntity.ok(TrainingMapper.mapToTrainerTrainingResponse(result));
    }



}
