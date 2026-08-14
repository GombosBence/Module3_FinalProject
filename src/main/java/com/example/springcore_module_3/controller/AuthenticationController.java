package com.example.springcore_module_3.controller;

import com.example.springcore_module_3.dto.request.LoginRequest;
import com.example.springcore_module_3.dto.request.UserPasswordChangeRequest;
import com.example.springcore_module_3.dto.response.LoginResponse;
import com.example.springcore_module_3.facade.GymFacade;
import com.example.springcore_module_3.util.JwtGenerator;
import com.example.springcore_module_3.util.TokenBlockList;
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
    private final JwtGenerator jwtGenerator;
    private final TokenBlockList tokenBlockList;

    public AuthenticationController(GymFacade gymFacade, JwtGenerator jwtGenerator, TokenBlockList tokenBlockList) {
        this.gymFacade = gymFacade;
        this.jwtGenerator = jwtGenerator;
        this.tokenBlockList = tokenBlockList;
    }

    @Operation(summary = "Login", description = "Username and password authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login"),
            @ApiResponse(responseCode = "401", description = "Authentication failed, invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest){
        gymFacade.login(loginRequest.username(), loginRequest.password());
        return ResponseEntity.ok(new LoginResponse(jwtGenerator.createToken(loginRequest.username())));
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

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader){
        String token = authHeader.substring(7);
        tokenBlockList.revokeToken(token);
        return ResponseEntity.ok().build();
    }

}
