package com.example.springcore_module_3.util;

import com.example.springcore_module_3.exception.UnAuthorizedAccessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class AuthorizationHelper {

    public void requireOwnAccount(String username){
        String loggedInUsername = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        if(!username.equals(loggedInUsername)){
            log.warn("User {} attempted to act on profile {}", loggedInUsername, username);
            throw new UnAuthorizedAccessException("You are not allowed to perform this action");
        }
    }

}
