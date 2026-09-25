package com.example.springcore_module_3.controller;

import com.example.springcore_module_3.dto.request.LoginRequest;
import com.example.springcore_module_3.dto.request.UserPasswordChangeRequest;
import com.example.springcore_module_3.exception.AuthenticationFailedException;
import com.example.springcore_module_3.facade.GymFacade;
import com.example.springcore_module_3.util.JwtGenerator;
import com.example.springcore_module_3.util.TokenBlockList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GymFacade gymFacade;

    @MockitoBean
    private JwtGenerator jwtGenerator;

    @MockitoBean
    private TokenBlockList tokenBlockList;

    @Test
    public void loginReturns200_whenCorrectCredentials() throws Exception {

        LoginRequest loginRequest = new LoginRequest("John.Doe", "Password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());

        verify(gymFacade).login("John.Doe", "Password123");
    }

    @Test
    public void loginReturns401_whenIncorrectCredentials() throws Exception {

        LoginRequest loginRequest = new LoginRequest("John.Doe", "Password123");

        doThrow(new AuthenticationFailedException("Invalid username or password"))
                .when(gymFacade).login("John.Doe", "Password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());

        verify(gymFacade).login("John.Doe", "Password123");
    }

    @Test
    public void changePasswordReturns200_whenCorrectCredentials() throws Exception {
        UserPasswordChangeRequest userPasswordChangeRequest =
                new UserPasswordChangeRequest("John.Doe", "old", "new");

        mockMvc.perform(put("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPasswordChangeRequest)))
                .andExpect(status().isOk());

        verify(gymFacade).changeUserPassword("John.Doe", "old", "new");
    }

    @Test
    public void changePasswordReturns401_whenIncorrectCredentials() throws Exception {
        UserPasswordChangeRequest userPasswordChangeRequest =
                new UserPasswordChangeRequest("John.Doe", "old", "new");

        doThrow(new AuthenticationFailedException("Invalid username or password")).when(gymFacade)
                .changeUserPassword("John.Doe", "old", "new");

        mockMvc.perform(put("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPasswordChangeRequest)))
                .andExpect(status().isUnauthorized());

        verify(gymFacade).changeUserPassword("John.Doe", "old", "new");
    }
}
