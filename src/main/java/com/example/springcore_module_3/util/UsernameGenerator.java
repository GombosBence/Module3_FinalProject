package com.example.springcore_module_3.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Slf4j
@Component
public class UsernameGenerator {

    public String generateUsername(String firstName, String lastName, Predicate<String> isUsernameTaken) {

        String usernameBase = firstName + "." + lastName;
        String username = usernameBase;

        int suffix = 1;
        while (isUsernameTaken.test(username)) {
            username = usernameBase + suffix++;
        }
        log.debug("Generated username: {} for {} {}", username, firstName, lastName);
        return username;
    }

}
