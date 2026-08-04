package com.example.springcore_module_3.controller;

import com.example.springcore_module_3.dto.request.TrainingCreationRequest;
import com.example.springcore_module_3.facade.GymFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingController.class)
public class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GymFacade gymFacade;


    @Test
    void addTraining_Returns200_success_test() throws Exception {

        TrainingCreationRequest request = new TrainingCreationRequest(
                "Trainee.user",
                "Trainer.user",
                "TrainingName",
                LocalDate.of(2026,11,11),
                Duration.ofMinutes(90)
        );

        mockMvc.perform(post("/api/training")
                        .header("X-Username", "John.Doe")
                        .header("X-Password", "Password123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void addTraining_returns404_whenNotFound() throws Exception {

        TrainingCreationRequest request = new TrainingCreationRequest(
                "Trainee.user",
                "Trainer.user",
                "TrainingName",
                LocalDate.of(2026,11,11),
                Duration.ofMinutes(90)
        );

        doThrow(new NoSuchElementException("Trainee with username Trainee.user not found"))
                .when(gymFacade)
                .createTraining(any(), eq("Trainee.user"), eq("Trainer.user"), any(), any(), any());

        mockMvc.perform(post("/api/training")
                        .header("X-Username", "John.Doe")
                        .header("X-Password", "Password123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

    }

    @Test
    void getTraineeTrainings_returns200_success_test() throws Exception {
        when(gymFacade.getTraineeTrainings(any(), eq("John.Doe"), any(), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/training/trainee/John.Doe/trainings")
                        .header("X-Username", "John.Doe")
                        .header("X-Password", "rawPassword")
                        .param("periodFrom", "2026-01-01")
                        .param("periodTo", "2026-12-31"))
                .andExpect(status().isOk());
    }

    @Test
    void getTrainerTrainings_returns200_success_test() throws Exception {
        when(gymFacade.getTrainerTrainings(any(), eq("Mike.Wilson"), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/training/trainer/Mike.Wilson/trainings")
                        .header("X-Username", "Mike.Wilson")
                        .header("X-Password", "rawPassword"))
                .andExpect(status().isOk());
    }


}
