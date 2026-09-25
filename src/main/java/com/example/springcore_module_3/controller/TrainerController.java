package com.example.springcore_module_3.controller;

import com.example.springcore_module_3.dto.TrainerCreationResult;
import com.example.springcore_module_3.dto.request.AuthenticationRequest;
import com.example.springcore_module_3.dto.request.TrainerRegistrationRequest;
import com.example.springcore_module_3.dto.request.TrainerSetActivateRequest;
import com.example.springcore_module_3.dto.request.TrainerUpdateProfileRequest;
import com.example.springcore_module_3.dto.response.TrainerGetProfileResponse;
import com.example.springcore_module_3.dto.response.TrainerRegistrationResponse;
import com.example.springcore_module_3.dto.response.TrainerUpdateResponse;
import com.example.springcore_module_3.facade.GymFacade;
import com.example.springcore_module_3.mapper.TrainerMapper;
import com.example.springcore_module_3.mapper.TrainingTypeMapper;
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

@Slf4j
@Tag(name = "Trainer", description = "Trainer registration and profile management")
@RestController
@RequestMapping("/api/trainer")
public class TrainerController {

    private final GymFacade gymFacade;

    public TrainerController(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }


    @Operation(summary = "Register a new trainer", description = "Creates a new trainer and returns the generated username and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainer successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping
    public ResponseEntity<TrainerRegistrationResponse> registerTrainer(@RequestBody @Valid TrainerRegistrationRequest request)
    {
        TrainerCreationResult result = gymFacade.createTrainer(request.firstName(), request.lastName(),
                TrainingTypeMapper.fromDto(request.specialization()));

        return ResponseEntity.status(HttpStatus.CREATED).body(new TrainerRegistrationResponse(
                result.trainer().getUser().getUsername(),
                result.rawPassword()));
    }


    @Operation(summary = "Get trainer profile", description = "Returns a trainer's profile with their assigned trainee's")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile received successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failed, invalid credentials"),
            @ApiResponse(responseCode = "404", description = "Requested trainer not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<TrainerGetProfileResponse> getTrainer(@PathVariable String username)
    {
        Trainer trainer = gymFacade.getTrainerByUsername(username);
        return ResponseEntity.ok(TrainerMapper.toGetProfileResponse(trainer));
    }


    @Operation(summary = "Update trainer profile", description = "Updated trainer's profile with the exception of username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Authentication failed, invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Not authorized to update the target profile"),
            @ApiResponse(responseCode = "404", description = "Requested trainer not found")
    })
    @PutMapping("/{username}")
    public ResponseEntity<TrainerUpdateResponse> updateTrainerProfile(@PathVariable String username,
                                                                      @RequestBody @Valid TrainerUpdateProfileRequest request)
    {
        Trainer trainer = gymFacade.updateTrainer(TrainerMapper.toTrainer(username, request));
        return ResponseEntity.ok(TrainerMapper.toUpdateResponse(trainer));
    }


    @Operation(summary = "Set active status", description = "Sets the trainer's active status to active/inactive")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active status successfully set"),
            @ApiResponse(responseCode = "409", description = "The trainer is already in the requested state"),
            @ApiResponse(responseCode = "401", description = "Authentication failed, invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Not authorized to update the target profile"),
            @ApiResponse(responseCode = "404", description = "Requested trainer not found")
    })
    @PatchMapping("/status")
    public ResponseEntity<Void> setTrainerActiveStatus(@RequestBody @Valid TrainerSetActivateRequest request)
    {
        if(request.isActive()){
            gymFacade.activateTrainer(request.username());

        }else {
            gymFacade.deactivateTrainer(request.username());
        }
        return ResponseEntity.ok().build();
    }
}
