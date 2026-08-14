package com.example.springcore_module_3.service;

public interface AuthenticationService {

    void authenticate(String username, String password);

    void changePassword(String username, String oldPassword, String newPassword);
}
