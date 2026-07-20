package com.example.springcore_module_3.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class User {

    private long userId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isActive;

}
