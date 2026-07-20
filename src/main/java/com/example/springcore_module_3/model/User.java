package com.example.springcore_module_3.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class User {

    private Long userId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isActive;


    protected User(String firstName, String lastName, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.isActive = true;
    }

}
