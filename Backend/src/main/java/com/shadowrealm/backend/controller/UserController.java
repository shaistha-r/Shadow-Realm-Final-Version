package com.shadowrealm.backend.controller;

import com.shadowrealm.backend.dto.LoginRequest;
import com.shadowrealm.backend.dto.LoginResponse;
import com.shadowrealm.backend.dto.UserResponse;
import com.shadowrealm.backend.entity.User;
import com.shadowrealm.backend.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody User user) {

        User saved = userService.register(user);
        return new UserResponse(saved.getId(), saved.getUsername(), saved.getEmail());
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        String token = userService.login(request);

        return new LoginResponse(token);
    }
}