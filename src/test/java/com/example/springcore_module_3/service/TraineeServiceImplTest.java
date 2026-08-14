package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dto.request.AuthenticationRequest;
import com.example.springcore_module_3.dto.TraineeCreationResult;
import com.example.springcore_module_3.exception.AuthenticationFailedException;
import com.example.springcore_module_3.exception.InvalidStateTransitionException;
import com.example.springcore_module_3.exception.UnAuthorizedAccessException;
import com.example.springcore_module_3.metrics.GymMetrics;
import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.TrainingType;
import com.example.springcore_module_3.model.User;
import com.example.springcore_module_3.repository.TraineeRepository;
import com.example.springcore_module_3.repository.TrainerRepository;
import com.example.springcore_module_3.repository.TrainingRepository;
import com.example.springcore_module_3.repository.UserRepository;
import com.example.springcore_module_3.util.AuthorizationHelper;
import com.example.springcore_module_3.util.PasswordGenerator;
import com.example.springcore_module_3.util.UsernameGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TraineeServiceImplTest {


    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private AuthorizationHelper authorizationHelper;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private GymMetrics gymMetrics;

    private TraineeServiceImpl traineeService;

    private TrainingType fitness() {
        TrainingType type = new TrainingType("FITNESS");
        type.setTrainingTypeId(1L);
        return type;
    }

    @BeforeEach
    void setUp() {
        traineeService = new TraineeServiceImpl(traineeRepository, passwordGenerator, usernameGenerator, passwordEncoder,
                authorizationHelper, trainingRepository, userRepository, trainerRepository, gymMetrics);

        Authentication auth = new UsernamePasswordAuthenticationToken("John.Doe", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void createTraineeProfileTest() {
        when(usernameGenerator.generateUsername(eq("John"), eq("Doe"), any()))
        .thenReturn("John.Doe");
        when(passwordGenerator.generatePassword(10)).thenReturn("ABCDE12345");

        User user = new User("John", "Doe", "John.Doe", "ABCDE12345");
        TraineeCreationResult result = traineeService.createTraineeProfile(user, "Budapest main street 1", LocalDate.of(1989, 4,11));

        assertEquals("John.Doe", result.trainee().getUser().getUsername());
        assertEquals("Budapest main street 1", result.trainee().getAddress());
        assertEquals(LocalDate.of(1989,4,11), result.trainee().getDateOfBirth());
        verify(passwordGenerator).generatePassword(10);
        verify(passwordEncoder).encode("ABCDE12345");
        verify(usernameGenerator).generateUsername(eq("John"), eq("Doe"), any());
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void updateTraineeProfileSuccessTest() {
        User user = new User("John", "Doe", "John.Doe", "ABCDE12345");
        Trainee updated = new Trainee(user, "NEW ADDRESS", LocalDate.of(1989, 4,11));
        Trainee old = new Trainee(user, "Old Address", LocalDate.of(1989, 4, 11));

        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(old));

        assertDoesNotThrow(() -> traineeService.updateTraineeProfile(updated));
        verify(traineeRepository).save(any(Trainee.class));
        verify(authorizationHelper).requireOwnAccount("John.Doe");
    }

    @Test
    void updateTraineeProfileAuthenticationFailedTest() {

        User user = new User("John", "Doe", "John.Doe", "hashedPw");
        Trainee incoming = new Trainee(user, "New Address", LocalDate.of(1989, 4, 11));

        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(incoming));
        doThrow(new UnAuthorizedAccessException("Invalid username or password"))
                .when(authorizationHelper).requireOwnAccount("John.Doe");

        assertThrows(UnAuthorizedAccessException.class,
                () -> traineeService.updateTraineeProfile(incoming));

        verify(traineeRepository, never()).save(any());
    }

    @Test
    void updateTraineeProfile_throwsUnauthorized_whenActingOnDifferentProfile() {
        User targetUser = new User("John", "Doe", "John.Doe", "hashedPw");
        Trainee targetTrainee = new Trainee(targetUser, "New Address", LocalDate.of(1989, 4, 11));

        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(targetTrainee));
        doThrow(new UnAuthorizedAccessException("You are not authorized to perform this operation"))
                .when(authorizationHelper).requireOwnAccount("John.Doe");

        assertThrows(UnAuthorizedAccessException.class,
                () -> traineeService.updateTraineeProfile(targetTrainee));

        verify(traineeRepository, never()).save(any());
    }

    @Test
    void updateTraineeProfile_doesNotRegenerateUsername_whenNameUnchanged() {
        User user = new User("John", "Doe", "John.Doe", "hashedPw");
        Trainee original = new Trainee(user, "Old Address", LocalDate.of(1989, 4, 11));
        Trainee incoming = new Trainee(user, "New Address", LocalDate.of(1989, 4, 11));

        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(original));

        traineeService.updateTraineeProfile(incoming);

        verify(usernameGenerator, never()).generateUsername(any(), any(), any());
        assertEquals("New Address", original.getAddress());
    }

    @Test
    void updateTraineeProfile_leavesFieldsUnchanged_whenNullPassed() {
        User user = new User("John", "Doe", "John.Doe", "hashedPw");
        Trainee original = new Trainee(user, "Original Address", LocalDate.of(1989, 4, 11));
        Trainee incoming = new Trainee(user, null, null);

        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(original));

        traineeService.updateTraineeProfile(incoming);

        assertEquals("Original Address", original.getAddress());
        assertEquals(LocalDate.of(1989, 4, 11), original.getDateOfBirth());
    }

    @Test
    void deleteTraineeProfileSuccessTest() {
        User user = new User("John", "Doe", "John.Doe", "hashedPw");
        Trainee trainee = new  Trainee(user, "Address", LocalDate.of(1989, 4, 11));

        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.deleteTraineeProfile("John.Doe"));
        verify(trainingRepository).deleteAllByTraineeUserUsername("John.Doe");
        verify(traineeRepository).delete(any(Trainee.class));
        verify(authorizationHelper).requireOwnAccount("John.Doe");
    }

    @Test
    void deleteTraineeProfileNotFoundTest() {
        when(traineeRepository.findByUserUsername(any())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> traineeService.deleteTraineeProfile("John.Doe"));
        verify(trainingRepository, never()).deleteAllByTraineeUserUsername(any());
        verify(traineeRepository, never()).delete(any(Trainee.class));
    }

    @Test
    void selectTraineeProfileByIdSuccessTest() {
        User user =  new User("John", "Doe", "John.Doe", "password");
        Trainee trainee = new  Trainee(user, "Address", LocalDate.of(1989, 4, 11));
        trainee.setTraineeId(1L);

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.selectTraineeProfile(1L));
    }

    @Test
    void selectTraineeProfileByIdFailureTest() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> traineeService.selectTraineeProfile(1L));
    }

    @Test
    void selectTraineeProfileByUsernameSuccessTest() {
        Trainee trainee = new  Trainee();
        trainee.setTraineeId(1L);

        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.selectTraineeProfileByUsername("John.Doe"));
    }

    @Test
    void selectTraineeProfileByUsernameFailureTest() {
        Trainee trainee = new  Trainee();
        trainee.setTraineeId(1L);

        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> traineeService.selectTraineeProfileByUsername("John.Doe"));
    }

    @Test
    void deactivateTraineeProfile_setsInactive_whenTraineeExists() {
        User user =  new User("John", "Doe", "John.Doe", "password");
        Trainee trainee = new  Trainee(user, "Address", LocalDate.of(1989, 4, 11));
        trainee.setTraineeId(1L);
        trainee.getUser().setActive(true);

        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.deactivateTraineeProfile("John.Doe"));

        assertFalse(trainee.getUser().isActive());
        verify(traineeRepository).save(trainee);
        verify(authorizationHelper).requireOwnAccount("John.Doe");
    }

    @Test
    void deactivateTraineeProfile_throws_whenTraineeDoesNotExist() {
        when(traineeRepository.findByUserUsername("wrong")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> traineeService.deactivateTraineeProfile("wrong"));

        verify(traineeRepository, never()).save(any());
    }

    @Test
    void deactivateThrows_whenAlreadyDeactivatedTrainee() {
        User user =  new User("John", "Doe", "John.Doe", "password");
        Trainee trainee = new  Trainee(user, "Address", LocalDate.of(1989, 4, 11));
        trainee.setTraineeId(1L);
        trainee.getUser().setActive(false);

        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));

        assertThrows(InvalidStateTransitionException.class, () -> traineeService.deactivateTraineeProfile("John.Doe"));
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void activateTraineeProfile_setsActive_whenTraineeExists() {
        User user =  new User("John", "Doe", "John.Doe", "password");
        Trainee trainee = new  Trainee(user, "Address", LocalDate.of(1989, 4, 11));
        trainee.setTraineeId(1L);
        trainee.getUser().setActive(false);

        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.activateTraineeProfile("John.Doe"));

        assertTrue(trainee.getUser().isActive());
        verify(traineeRepository).save(trainee);
        verify(authorizationHelper).requireOwnAccount("John.Doe");
    }

    @Test
    void activateTraineeProfile_throws_whenTraineeDoesNotExist() {
        when(traineeRepository.findByUserUsername("wrong")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> traineeService.activateTraineeProfile("wrong"));

        verify(traineeRepository, never()).save(any());
    }

    @Test
    void activateThrows_whenAlreadyActivatedTrainee() {
        User user =  new User("John", "Doe", "John.Doe", "password");
        Trainee trainee = new  Trainee(user, "Address", LocalDate.of(1989, 4, 11));
        trainee.setTraineeId(1L);
        trainee.getUser().setActive(true);

        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));

        assertThrows(InvalidStateTransitionException.class, () -> traineeService.activateTraineeProfile("John.Doe"));
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void updateTraineeTrainers_succeeds_whenAllTrainerIdsExist() {
        Trainee trainee = new Trainee(new User("John", "Doe", "John.Doe", "hashedPw"), "Addr", LocalDate.of(1989, 4, 11));
        Trainer trainer1 = new Trainer(new User("Mike", "Wilson", "Mike.Wilson", "hashedPw"), fitness());
        Trainer trainer2 = new Trainer(new User("Sara", "Connor", "Sara.Connor", "hashedPw"), fitness());

        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAllByUserUsernameIn(List.of("user.name1", "user.name2"))).thenReturn(List.of(trainer1, trainer2));

        assertDoesNotThrow(() -> traineeService.updateTraineeTrainers("John.Doe", List.of("user.name1", "user.name2")));

        assertEquals(2, trainee.getTrainers().size());
        verify(traineeRepository).save(trainee);
        verify(authorizationHelper).requireOwnAccount("John.Doe");
    }

    @Test
    void updateTraineeTrainers_throws_whenSomeTrainerIdsDoNotExist() {
        Trainee trainee = new Trainee(new User("John", "Doe", "John.Doe", "hashedPw"), "Addr", LocalDate.of(1989, 4, 11));
        Trainer trainer1 = new Trainer(new User("Mike", "Wilson", "Mike.Wilson", "hashedPw"), fitness());

        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAllByUserUsernameIn(List.of("Mike.Wilson", "wrong"))).thenReturn(List.of(trainer1));

        assertThrows(NoSuchElementException.class,
                () -> traineeService.updateTraineeTrainers("John.Doe", List.of("Mike.Wilson", "wrong")));

        verify(traineeRepository, never()).save(any());
    }

    @Test
    void selectUnassignedTrainers_returnsListFromRepository() {
        List<Trainer> expected = List.of(new Trainer(new User("Sara", "Connor", "Sara.Connor", "hashedPw"), fitness()));

        when(trainerRepository.findUnassignedTrainers("John.Doe")).thenReturn(expected);

        List<Trainer> result = traineeService.selectUnassignedTrainers("John.Doe");

        assertEquals(1, result.size());
    }

}
