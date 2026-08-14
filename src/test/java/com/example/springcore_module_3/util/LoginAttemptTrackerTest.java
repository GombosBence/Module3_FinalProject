package com.example.springcore_module_3.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class LoginAttemptTrackerTest {

    @InjectMocks
    private LoginAttemptTracker loginAttemptTracker;


    @Test
    void loginAttemptTracker_LocksOutAfterThreeFails(){

       loginAttemptTracker.recordFailure("John.Doe");
       loginAttemptTracker.recordFailure("John.Doe");
       loginAttemptTracker.recordFailure("John.Doe");

       assertTrue(loginAttemptTracker.isLocked("John.Doe"));
    }

    @Test
    void loginAttemptTracker_ResetsAfterSuccessfulLogin(){
        loginAttemptTracker.recordFailure("John.Doe");
        loginAttemptTracker.recordFailure("John.Doe");
        loginAttemptTracker.recordSuccess("John.Doe");
        loginAttemptTracker.recordFailure("John.Doe");

        assertFalse(loginAttemptTracker.isLocked("John.Doe"));
    }

}
