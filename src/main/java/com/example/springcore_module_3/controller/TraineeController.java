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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Trainee", description = "Trainee registration and profile management")
@RestController
@RequestMapping("/api/trainee")
public class TraineeController {

    private final GymFacade  gymFacade;

    public TraineeController(GymFacade gymFacade){
        this.gymFacade = gymFacade;
    }


    @Operation(summary = "Register a new trainee", description = "Creates a new trainee and returns the generated password and username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainee successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping
    public ResponseEntity<TraineeRegistrationResponse> register(@RequestBody @Valid TraineeRegistrationRequest request){

        TraineeCreationResult result  = gymFacade.createTrainee(request.firstname(), request.lastname(),
                request.address(), request.dateOfBirth());

        TraineeRegistrationResponse response = new TraineeRegistrationResponse(result.trainee().getUser().getUsername(), result.rawPassword());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "Get trainee profile", description = "Returns a trainee's profile and assigned trainers based on username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile received successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failed, invalid credentials"),
            @ApiResponse(responseCode = "404", description = "Requested trainee not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<TraineeGetProfileResponse> getTrainee(@PathVariable String username)
    {
        Trainee trainee = gymFacade.getTraineeByUsername(username);
        return ResponseEntity.ok(TraineeMapper.toProfileResponse(trainee));
    }


    @Operation(summary = "Update trainee profile", description = "Updates the trainee profile with the exception of username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Authentication failed, invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Not authorized to update the target profile"),
            @ApiResponse(responseCode = "404", description = "Requested trainee not found")
    })
    @PutMapping("/{username}")
    public ResponseEntity<TraineeUpdateResponse> updateTrainee(@PathVariable String username, @RequestBody @Valid TraineeUpdateRequest request)
    {
        Trainee trainee = gymFacade.updateTrainee(TraineeMapper.toTrainee(username, request));
        return ResponseEntity.ok(TraineeMapper.toUpdateResponse(trainee));
    }


    @Operation(summary = "Delete trainee profile", description = "Hard-deletes the trainee profile and their related trainings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication failed, invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Not authorized to delete the target profile"),
            @ApiResponse(responseCode = "404", description = "Requested trainee not found")
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainee(@PathVariable String username)
    {
        gymFacade.deleteTrainee(username);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Get trainee's unassigned trainers", description = "Returns a list of unassigned trainers for trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unassigned trainers listed for trainee"),
            @ApiResponse(responseCode = "401", description = "Authentication failed, invalid credentials"),
            @ApiResponse(responseCode = "404", description = "Requested trainee not found")
    })
    @GetMapping("/{username}/unassigned-trainers")
    public ResponseEntity<List<TrainerProfileDto>> getTraineeUnassignedTrainers(@PathVariable String username)
    {
        List<Trainer> result = gymFacade.getUnassignedTrainers(username);
        List<TrainerProfileDto> profiles = result.stream().map(TrainerMapper::toProfileDto).toList();
        return ResponseEntity.ok(profiles);
    }


    @Operation(summary = "Update trainee's trainer list", description = "Overwrites the trainee's trainer list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer list successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Authentication failed, invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Not authorized to update the target profile's trainers"),
            @ApiResponse(responseCode = "404", description = "Requested trainee not found")
    })
    @PutMapping("/{username}/trainers")
    public ResponseEntity<List<TrainerProfileDto>> updateTraineeTrainerList(@PathVariable String username,
                                                                            @RequestBody @Valid UpdateTraineeTrainersRequest request)
    {
        List<Trainer> result = gymFacade.updateTraineeTrainers(username ,request.trainerUsernames());
        List<TrainerProfileDto> profiles = result.stream().map(TrainerMapper::toProfileDto).toList();
        return ResponseEntity.ok(profiles);
    }

    @Operation(summary = "Set trainee's active status", description = "Sets the trainee's active status to active/inactive")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active status successfully set"),
            @ApiResponse(responseCode = "409", description = "The trainee is already in the requested state"),
            @ApiResponse(responseCode = "401", description = "Authentication failed, invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Not authorized to update the target profile"),
            @ApiResponse(responseCode = "404", description = "Requested trainee not found")
    })
    @PatchMapping("/status")
    public ResponseEntity<Void> setTraineeActiveStatus(@RequestBody @Valid TraineeSetActivateRequest request) {

        if (request.isActive()) {
            gymFacade.activateTrainee(request.username());
        } else {
            gymFacade.deactivateTrainee(request.username());
        }
        return ResponseEntity.ok().build();
    }
}
