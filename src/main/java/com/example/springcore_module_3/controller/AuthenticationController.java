package com.example.springcore_module_3.controller;

import com.example.springcore_module_3.dto.request.UserPasswordChangeRequest;
import com.example.springcore_module_3.facade.GymFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Authentication", description = "Authentication and authorization for the users")
@RestController
@RequestMapping("api/auth")
public class AuthenticationController {

    private final GymFacade gymFacade;

    public AuthenticationController(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }

    @Operation(summary = "Login", description = "Username and password authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login"),
            @ApiResponse(responseCode = "401", description = "Authentication failed, invalid credentials")
    })
    @GetMapping
    public ResponseEntity<Void> login(@RequestHeader("X-Username") String username,
                                      @RequestHeader("X-Password") String password) {
        gymFacade.login(username, password);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Password change", description = "Changes the user's password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful password change"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Authentication failed, invalid credentials")
    })
    @PutMapping
    public ResponseEntity<Void> changeUserPassword(@RequestBody @Valid UserPasswordChangeRequest request) {

        gymFacade.changeUserPassword(request.username(), request.oldPassword(), request.newPassword());
        return ResponseEntity.ok().build();
    }

}
