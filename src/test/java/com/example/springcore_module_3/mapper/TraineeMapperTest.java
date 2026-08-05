package com.example.springcore_module_3.mapper;

import com.example.springcore_module_3.dto.request.TraineeUpdateRequest;
import com.example.springcore_module_3.dto.response.TraineeGetProfileResponse;
import com.example.springcore_module_3.dto.response.TraineeUpdateResponse;
import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.TrainingType;
import com.example.springcore_module_3.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TraineeMapperTest {

    private TrainingType fitness() {
        TrainingType type = new TrainingType("FITNESS");
        type.setTrainingTypeId(1L);
        return type;
    }

    private Trainer sampleTrainer(String username) {
        return new Trainer(new User("Mike", "Wilson", username, "hashedPw"), fitness());
    }

    @Test
    void toProfileResponse_mapsAllFieldsCorrectly() {
        User user = new User("John", "Doe", "John.Doe", "hashedPw");
        user.setActive(true);
        Trainee trainee = new Trainee(user, "123 Main St", LocalDate.of(2000, 1, 1));
        trainee.getTrainers().add(sampleTrainer("Mike.Wilson"));

        TraineeGetProfileResponse response = TraineeMapper.toProfileResponse(trainee);

        assertEquals("John", response.firstName());
        assertEquals("Doe", response.lastName());
        assertEquals(LocalDate.of(2000, 1, 1), response.dateOfBirth());
        assertEquals("123 Main St", response.address());
        assertTrue(response.isActive());
        assertEquals(1, response.trainerProfiles().size());
        assertEquals("Mike.Wilson", response.trainerProfiles().get(0).username());
    }

    @Test
    void toProfileResponse_returnsEmptyTrainerList_whenNoTrainersAssigned() {
        User user = new User("John", "Doe", "John.Doe", "hashedPw");
        Trainee trainee = new Trainee(user, "Addr", LocalDate.of(2000, 1, 1));

        TraineeGetProfileResponse response = TraineeMapper.toProfileResponse(trainee);

        assertTrue(response.trainerProfiles().isEmpty());
    }

    @Test
    void toTrainee_buildsCorrectTraineeFromRequest() {
        TraineeUpdateRequest request = new TraineeUpdateRequest("John", "Doe",
                LocalDate.of(2000, 1, 1), "123 Main St", true);

        Trainee trainee = TraineeMapper.toTrainee("John.Doe", request);

        assertEquals("John.Doe", trainee.getUser().getUsername());
        assertEquals("John", trainee.getUser().getFirstName());
        assertEquals("Doe", trainee.getUser().getLastName());
        assertTrue(trainee.getUser().isActive());
        assertEquals("123 Main St", trainee.getAddress());
        assertEquals(LocalDate.of(2000, 1, 1), trainee.getDateOfBirth());
    }

    @Test
    void toUpdateResponse_mapsAllFieldsCorrectly() {
        User user = new User("John", "Doe", "John.Doe", "hashedPw");
        user.setActive(false);
        Trainee trainee = new Trainee(user, "123 Main St", LocalDate.of(2000, 1, 1));
        trainee.getTrainers().add(sampleTrainer("Sara.Connor"));

        TraineeUpdateResponse response = TraineeMapper.toUpdateResponse(trainee);

        assertEquals("John.Doe", response.username());
        assertEquals("John", response.firstName());
        assertEquals("Doe", response.lastName());
        assertFalse(response.isActive());
        assertEquals(1, response.trainerProfiles().size());
        assertEquals("Sara.Connor", response.trainerProfiles().get(0).username());
    }

    @Test
    void toProfileDto_mapsFieldsCorrectly() {
        User user = new User("John", "Doe", "John.Doe", "hashedPw");
        Trainee trainee = new Trainee(user, "Addr", LocalDate.of(2000, 1, 1));

        var dto = TraineeMapper.toProfileDto(trainee);

        assertEquals("John.Doe", dto.username());
        assertEquals("John", dto.firstName());
        assertEquals("Doe", dto.lastName());
    }
}