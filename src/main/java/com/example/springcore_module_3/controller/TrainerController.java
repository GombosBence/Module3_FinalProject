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
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/trainer")
public class TrainerController {

    private final GymFacade gymFacade;

    public TrainerController(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }

    @PostMapping
    public ResponseEntity<TrainerRegistrationResponse> registerTrainer(@RequestBody @Valid TrainerRegistrationRequest request)
    {
        TrainerCreationResult result = gymFacade.createTrainer(request.firstName(), request.lastName(),
                TrainingTypeMapper.fromDto(request.specialization()));

        return ResponseEntity.status(HttpStatus.CREATED).body(new TrainerRegistrationResponse(
                result.trainer().getUser().getUsername(),
                result.rawPassword()));
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerGetProfileResponse> getTrainer(@PathVariable String username,
                                                                @RequestHeader("X-Username") String authUsername,
                                                                @RequestHeader("X-Password") String authPassword)
    {
        AuthenticationRequest credentials = new AuthenticationRequest(authUsername, authPassword);
        Trainer trainer = gymFacade.getTrainerByUsername(credentials, username);

        return ResponseEntity.ok(TrainerMapper.toGetProfileResponse(trainer));
    }

    @PutMapping("/{username}")
    public ResponseEntity<TrainerUpdateResponse> updateTrainerProfile(@PathVariable String username,
                                                                      @RequestHeader("X-Username") String authUsername,
                                                                      @RequestHeader("X-Password") String authPassword,
                                                                      @RequestBody @Valid TrainerUpdateProfileRequest request)
    {
        AuthenticationRequest credentials = new AuthenticationRequest(authUsername, authPassword);
        Trainer trainer = gymFacade.updateTrainer(credentials, TrainerMapper.toTrainer(username, request));

        return ResponseEntity.ok(TrainerMapper.toUpdateResponse(trainer));

    }

    @PatchMapping("/status")
    public ResponseEntity<Void> setTrainerActiveStatus(@RequestHeader("X-Username") String authUsername,
                                                       @RequestHeader("X-Password") String authPassword,
                                                       @RequestBody @Valid TrainerSetActivateRequest request)
    {
        AuthenticationRequest credentials = new AuthenticationRequest(authUsername, authPassword);
        if(request.isActive()){
            gymFacade.activateTrainer(credentials, request.username());

        }else {
            gymFacade.deactivateTrainer(credentials, request.username());
        }

        return ResponseEntity.ok().build();
    }
}
