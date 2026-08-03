package com.example.springcore_module_3.controller;

import com.example.springcore_module_3.dto.request.AuthenticationRequest;
import com.example.springcore_module_3.dto.request.UserPasswordChangeRequest;
import com.example.springcore_module_3.facade.GymFacade;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/auth")
public class AuthenticationController {

    private final GymFacade gymFacade;

    public AuthenticationController(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }

    @GetMapping
    public ResponseEntity<Void> login(@RequestParam String username, @RequestParam String password) {
        gymFacade.login(username, password);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> changeUserPassword(@RequestBody @Valid UserPasswordChangeRequest request) {

        gymFacade.changeUserPassword(request.username(), request.oldPassword(), request.newPassword());
        return ResponseEntity.ok().build();
    }

}
