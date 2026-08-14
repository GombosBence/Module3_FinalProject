package com.example.springcore_module_3.controller;

import com.example.springcore_module_3.dto.TrainingTypeDto;
import com.example.springcore_module_3.dto.request.AuthenticationRequest;
import com.example.springcore_module_3.facade.GymFacade;
import com.example.springcore_module_3.mapper.TrainingTypeMapper;
import com.example.springcore_module_3.model.TrainingType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "Training type", description = "Lists the existing training types/specializations")
@RestController
@RequestMapping("/api/training-type")
public class TrainingTypeController {


    private final GymFacade  gymFacade;

    public TrainingTypeController(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }

    @Operation(summary = "List training types", description = "Gives back a list of existing training types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned the list of training types"),
            @ApiResponse(responseCode = "401", description = "Authentication failed, invalid credentials")
    })
    @GetMapping
    public ResponseEntity<List<TrainingTypeDto>> getTrainingTypes()
    {
        List<TrainingType> trainingTypes = gymFacade.getTrainingTypes();
        List<TrainingTypeDto> response = trainingTypes.stream().map(TrainingTypeMapper::toDto).toList();
        return ResponseEntity.ok(response);
    }

}
