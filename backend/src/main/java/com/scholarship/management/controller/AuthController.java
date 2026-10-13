package com.scholarship.management.controller;

import com.scholarship.management.dto.LoginRequest;
import com.scholarship.management.dto.UserRequest;
import com.scholarship.management.model.User;
import com.scholarship.management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** Register and Login pages call these. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /** POST /api/auth/register - a new student creates an account */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@Valid @RequestBody UserRequest request) {
        return userService.createStudent(request);
    }

    /** POST /api/auth/login - returns the user, including the role (ADMIN or STUDENT) */
    @PostMapping("/login")
    public User login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }
}
