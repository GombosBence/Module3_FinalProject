package com.example.springcore_module_3.mapper;

import com.example.springcore_module_3.model.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainingMapperTest {

    private TrainingType fitness() {
        TrainingType type = new TrainingType("FITNESS");
        type.setTrainingTypeId(1L);
        return type;
    }

    private Training sampleTraining() {
        Trainee trainee = new Trainee(new User("John", "Doe", "John.Doe", "hashedPw"), "Addr", LocalDate.of(2000, 1, 1));
        Trainer trainer = new Trainer(new User("Mike", "Wilson", "Mike.Wilson", "hashedPw"), fitness());
        return new Training(trainee, trainer, "Morning Session", fitness(),
                LocalDate.of(2026, 3, 14), Duration.ofMinutes(60));
    }

    @Test
    void mapTrainingToTraineeTrainingDto_mapsCorrectly() {
        var dto = TrainingMapper.mapTrainingToTraineeTrainingDto(sampleTraining());

        assertEquals("Morning Session", dto.trainingName());
        assertEquals(LocalDate.of(2026, 3, 14), dto.date());
        assertEquals("FITNESS", dto.trainingType().name());
        assertEquals(Duration.ofMinutes(60), dto.trainingDuration());
        assertEquals("Mike.Wilson", dto.trainerName());
    }

    @Test
    void mapTrainingToTrainerTrainingDto_mapsCorrectly() {
        var dto = TrainingMapper.mapTrainingToTrainerTrainingDto(sampleTraining());

        assertEquals("Morning Session", dto.trainingName());
        assertEquals("John.Doe", dto.traineeName());
    }

    @Test
    void mapToTraineeTrainingResponse_mapsListCorrectly() {
        List<Training> trainings = List.of(sampleTraining(), sampleTraining());

        var result = TrainingMapper.mapToTraineeTrainingResponse(trainings);

        assertEquals(2, result.size());
    }

    @Test
    void mapToTrainerTrainingResponse_mapsListCorrectly() {
        List<Training> trainings = List.of(sampleTraining());

        var result = TrainingMapper.mapToTrainerTrainingResponse(trainings);

        assertEquals(1, result.size());
    }
}