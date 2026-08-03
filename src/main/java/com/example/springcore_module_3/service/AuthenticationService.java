package com.example.springcore_module_3.service;

import com.example.springcore_module_3.dto.request.AuthenticationRequest;

public interface AuthenticationService {

    void authenticate(String username, String password);

    void authenticateAndAuthorize(String username, String password, String targetUsername);

    void changePassword(String username, String oldPassword, String newPassword);
}
