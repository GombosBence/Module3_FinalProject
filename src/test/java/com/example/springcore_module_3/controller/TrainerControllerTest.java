package com.example.springcore_module_3.controller;
import com.example.springcore_module_3.dto.TrainerCreationResult;
import com.example.springcore_module_3.dto.TrainingTypeDto;
import com.example.springcore_module_3.dto.request.TrainerRegistrationRequest;
import com.example.springcore_module_3.dto.request.TrainerSetActivateRequest;
import com.example.springcore_module_3.dto.request.TrainerUpdateProfileRequest;
import com.example.springcore_module_3.exception.AuthenticationFailedException;
import com.example.springcore_module_3.exception.InvalidStateTransitionException;
import com.example.springcore_module_3.facade.GymFacade;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.TrainingType;
import com.example.springcore_module_3.model.User;
import com.example.springcore_module_3.util.JwtGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainerController.class)
public class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GymFacade gymFacade;

    @MockitoBean
    private JwtGenerator jwtGenerator;

    private User sampleUser(String username) {
        return new User("John", "Doe", username, "hashedPw");
    }

    private TrainingType sampleTrainingType(String name) {
        return new TrainingType(name);
    }

    @Test
    void registerReturns_201_Correct_request() throws Exception {
        TrainerRegistrationRequest trainerRegistrationRequest = new TrainerRegistrationRequest("John", "Doe",
                new TrainingTypeDto(1L, "Fitness"));

        Trainer trainer = new Trainer(sampleUser("John.Doe"), sampleTrainingType("Fitness"));
        TrainerCreationResult result = new TrainerCreationResult(trainer, "password");

        when(gymFacade.createTrainer(eq("John"), eq("Doe"), any(TrainingType.class))).thenReturn(result);

        mockMvc.perform(post("/api/trainer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trainerRegistrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("John.Doe"))
                .andExpect(jsonPath("$.password").value("password"));

        verify(gymFacade).createTrainer(eq("John"), eq("Doe"), any(TrainingType.class));
    }

    @Test
    void registerReturns_400_Bad_Request() throws Exception {

        String invalidJson = """
                {"lastname": "Doe", "address": "Addr"}
                """;

        mockMvc.perform(post("/api/trainer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(gymFacade, never()).createTrainer(any(), any(), any(TrainingType.class));
    }

    @Test
    void getTrainerProfile_returns200_correct_response() throws Exception {

        Trainer trainer = new Trainer(sampleUser("John.Doe"), sampleTrainingType("Fitness"));

        when(gymFacade.getTrainerByUsername(eq("John.Doe"))).thenReturn(trainer);

        mockMvc.perform(get("/api/trainer/John.Doe")
                        .with(user("John.Doe")))
                .andExpect(status().isOk());

        verify(gymFacade).getTrainerByUsername(eq("John.Doe"));
    }

    @Test
    void getTrainerProfile_returns401_authentication_fails() throws Exception {

        doThrow(new AuthenticationFailedException("Invalid username or password"))
                .when(gymFacade).getTrainerByUsername(any());

        mockMvc.perform(get("/api/trainer/wrong.Doe")
                        .with(user("John.Doe")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getTrainerProfile_returns404_whenNotFound() throws Exception {

        when(gymFacade.getTrainerByUsername(eq("Wrong.User"))).thenThrow(new NoSuchElementException("User not found"));

        mockMvc.perform(get("/api/trainer/Wrong.User")
                        .with(user("John.Doe")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void updateTrainerProfile_returns200_correct_response() throws Exception {

        TrainerUpdateProfileRequest request = new TrainerUpdateProfileRequest("John", "Doe",
                new TrainingTypeDto(1L, "Fitness"), true);

        Trainer trainer =  new Trainer(sampleUser("John.Doe"), sampleTrainingType("Fitness"));

        when(gymFacade.updateTrainer(any())).thenReturn(trainer);

        mockMvc.perform(put("/api/trainer/John.Doe")
                        .with(user("John.Doe"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(gymFacade).updateTrainer(any());
    }

    @Test
    void updateTrainerProfile_returns401_authentication_fails() throws Exception {

        TrainerUpdateProfileRequest request = new TrainerUpdateProfileRequest("John", "Doe",
                new TrainingTypeDto(1L, "Fitness"), true);

        when(gymFacade.updateTrainer(any())).thenThrow(new AuthenticationFailedException("Invalid username or password"));

        mockMvc.perform(put("/api/trainer/Wrong.User")
                        .with(user("John.Doe"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void updateTrainerProfile_returns404_whenNotFound() throws Exception {

        TrainerUpdateProfileRequest request = new TrainerUpdateProfileRequest("John", "Doe",
                new TrainingTypeDto(1L, "Fitness"), true);

        when(gymFacade.updateTrainer(any())).thenThrow(new NoSuchElementException("User not found"));

        mockMvc.perform(put("/api/trainer/Wrong.User")
                        .with(user("John.Doe"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));

    }

    @Test
    void activateTrainerProfile_returns200_correct_response() throws Exception {

        TrainerSetActivateRequest request = new TrainerSetActivateRequest("John.Doe", true);

        mockMvc.perform(patch("/api/trainer/status")
                        .with(user("John.Doe"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(gymFacade).activateTrainer(any());
    }

    @Test
    void activateTrainerProfile_returns401_authentication_fails() throws Exception {

        TrainerSetActivateRequest request = new TrainerSetActivateRequest("John.Doe", true);

        doThrow(new AuthenticationFailedException("Invalid username or password")).when(gymFacade).activateTrainer(any());

        mockMvc.perform(patch("/api/trainer/status")
                        .with(user("John.Doe"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void activateTrainerProfile_returns404_whenNotFound() throws Exception {

        TrainerSetActivateRequest request = new TrainerSetActivateRequest("John.Doe", false);

        doThrow(new NoSuchElementException("User not found")).when(gymFacade).deactivateTrainer(any());

        mockMvc.perform(patch("/api/trainer/status")
                        .with(user("John.Doe"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deactivateTrainerProfile_returns409_whenAlreadyInactive() throws Exception {

        TrainerSetActivateRequest request = new TrainerSetActivateRequest("John", false);

        doThrow(new InvalidStateTransitionException("Trainer already inactive")).when(gymFacade).deactivateTrainer(any());

        mockMvc.perform(patch("/api/trainer/status")
                        .with(user("John.Doe"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Trainer already inactive"));
    }

}
