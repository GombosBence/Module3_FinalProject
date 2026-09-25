package com.example.springcore_module_3.util;

import com.example.springcore_module_3.exception.UnAuthorizedAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class AuthorizationHelperTest {

    @InjectMocks
    private AuthorizationHelper authorizationHelper;

    @BeforeEach
    public void setup() {
        Authentication auth = new UsernamePasswordAuthenticationToken("John.Doe", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void requireOwnAccount_success_ifUserMatches(){
        assertDoesNotThrow(() -> authorizationHelper.requireOwnAccount("John.Doe"));
    }

    @Test
    void requireOwnAccount_fail_ifUserDoesNotMatch(){
        assertThrows(UnAuthorizedAccessException.class, () -> authorizationHelper.requireOwnAccount("Wrong.Doe"));
    }

}
