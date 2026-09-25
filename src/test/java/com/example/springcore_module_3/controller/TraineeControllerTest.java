package com.example.springcore_module_3.controller;

import com.example.springcore_module_3.dto.TraineeCreationResult;
import com.example.springcore_module_3.dto.request.TraineeRegistrationRequest;
import com.example.springcore_module_3.facade.GymFacade;
import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.User;
import com.example.springcore_module_3.util.JwtGenerator;
import com.example.springcore_module_3.util.TokenBlockList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TraineeController.class)
public class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GymFacade gymFacade;

    @MockitoBean
    private JwtGenerator jwtGenerator;

    @MockitoBean
    private TokenBlockList tokenBlockList;

    private User sampleUser(String username) {
        return new User("John", "Doe", username, "hashedPw");
    }

    @Test
    void register_returns201_withUsernameAndPassword() throws Exception {

        TraineeRegistrationRequest request = new TraineeRegistrationRequest("John", "Doe", "Addr",
                LocalDate.of(1989, 4, 3));

        Trainee trainee = new Trainee(sampleUser("John.Doe"), "Addr", LocalDate.of(1989, 4, 3));
        TraineeCreationResult result = new TraineeCreationResult(trainee, "password");

        when(gymFacade.createTrainee("John", "Doe", "Addr",
                LocalDate.of(1989, 4, 3))).thenReturn(result);

        mockMvc.perform(post("/api/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("John.Doe"))
                .andExpect(jsonPath("$.password").value("password"));
    }

    @Test
    void register_returns400_whenFirstNameMissing() throws Exception {
        String invalidJson = """
                {"lastname": "Doe", "address": "Addr"}
                """;

        mockMvc.perform(post("/api/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(gymFacade, never()).createTrainee(any(), any(), any(), any());
    }

    @Test
    void getTrainee_returns200_withProfile() throws Exception {
        Trainee trainee = new Trainee(sampleUser("John.Doe"), "Addr", LocalDate.of(1989, 4, 11));

        when(gymFacade.getTraineeByUsername(eq("John.Doe"))).thenReturn(trainee);

        mockMvc.perform(get("/api/trainee/John.Doe")
                        .with(user("John.Doe")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void getTrainee_returns404_whenNotFound() throws Exception {
        when(gymFacade.getTraineeByUsername(eq("Nobody.Here")))
                .thenThrow(new NoSuchElementException("Trainee with username Nobody.Here does not exist"));

        mockMvc.perform(get("/api/trainee/Nobody.Here")
                        .with(user("John.Doe")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Trainee with username Nobody.Here does not exist"));
    }

    @Test
    void deleteTrainee_returns200() throws Exception {
        mockMvc.perform(delete("/api/trainee/John.Doe")
                        .with(user("John.Doe")))
                .andExpect(status().isOk());

        verify(gymFacade).deleteTrainee("John.Doe");
    }

    @Test
    void setTraineeActiveStatus_callsActivate_whenIsActiveTrue() throws Exception {
        String requestBody = """
                {"username": "John.Doe", "isActive": true}
                """;

        mockMvc.perform(patch("/api/trainee/status")
                        .with(user("John.Doe"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(gymFacade).activateTrainee("John.Doe");
        verify(gymFacade, never()).deactivateTrainee(any());
    }

    @Test
    void setTraineeActiveStatus_callsDeactivate_whenIsActiveFalse() throws Exception {
        String requestBody = """
                {"username": "John.Doe", "isActive": false}
                """;

        mockMvc.perform(patch("/api/trainee/status")
                        .with(user("John.Doe"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(gymFacade).deactivateTrainee("John.Doe");
        verify(gymFacade, never()).activateTrainee(any());
    }
}