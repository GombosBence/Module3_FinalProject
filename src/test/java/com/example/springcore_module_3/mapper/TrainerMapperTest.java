package com.example.springcore_module_3.mapper;

import com.example.springcore_module_3.dto.request.TrainerUpdateProfileRequest;
import com.example.springcore_module_3.dto.TrainingTypeDto;
import com.example.springcore_module_3.dto.response.TrainerGetProfileResponse;
import com.example.springcore_module_3.dto.response.TrainerUpdateResponse;
import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.TrainingType;
import com.example.springcore_module_3.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TrainerMapperTest {

    private TrainingType fitness() {
        TrainingType type = new TrainingType("FITNESS");
        type.setTrainingTypeId(1L);
        return type;
    }

    @Test
    void toProfileDto_mapsAllFieldsCorrectly() {
        User user = new User("Mike", "Wilson", "Mike.Wilson", "hashedPw");
        Trainer trainer = new Trainer(user, fitness());

        var dto = TrainerMapper.toProfileDto(trainer);

        assertEquals("Mike.Wilson", dto.username());
        assertEquals("Mike", dto.firstName());
        assertEquals("Wilson", dto.lastName());
        assertEquals(1L, dto.specialization().id());
        assertEquals("FITNESS", dto.specialization().name());
    }

    @Test
    void toGetProfileResponse_mapsAllFieldsCorrectly() {
        User user = new User("Mike", "Wilson", "Mike.Wilson", "hashedPw");
        user.setActive(true);
        Trainer trainer = new Trainer(user, fitness());
        Trainee trainee = new Trainee(new User("John", "Doe", "John.Doe", "hashedPw"), "Addr", LocalDate.of(2000, 1, 1));
        trainer.getTrainees().add(trainee);

        TrainerGetProfileResponse response = TrainerMapper.toGetProfileResponse(trainer);

        assertEquals("Mike", response.firstName());
        assertEquals("Wilson", response.lastName());
        assertEquals("FITNESS", response.specialization().name());
        assertTrue(response.isActive());
        assertEquals(1, response.trainees().size());
        assertEquals("John.Doe", response.trainees().get(0).username());
    }

    @Test
    void toUpdateResponse_mapsAllFieldsCorrectly() {
        User user = new User("Mike", "Wilson", "Mike.Wilson", "hashedPw");
        user.setActive(false);
        Trainer trainer = new Trainer(user, fitness());

        TrainerUpdateResponse response = TrainerMapper.toUpdateResponse(trainer);

        assertEquals("Mike.Wilson", response.username());
        assertEquals("Mike", response.firstName());
        assertFalse(response.isActive());
        assertTrue(response.trainees().isEmpty());
    }

    @Test
    void toTrainer_buildsCorrectTrainerFromRequest() {
        TrainerUpdateProfileRequest request = new TrainerUpdateProfileRequest(
                "Mike", "Wilson", new TrainingTypeDto(1L, "FITNESS"), true);

        Trainer trainer = TrainerMapper.toTrainer("Mike.Wilson", request);

        assertEquals("Mike.Wilson", trainer.getUser().getUsername());
        assertEquals("Mike", trainer.getUser().getFirstName());
        assertEquals("FITNESS", trainer.getSpecialization().getTrainingTypeName());
        assertEquals(1L, trainer.getSpecialization().getTrainingTypeId());
    }
}