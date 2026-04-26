package com.bank.adapter.in.web.controller;

import com.bank.application.dto.BankingDto.ApiResponse;
import com.bank.application.dto.UserDto.*;
import com.bank.application.port.input.UserInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ADAPTER (Driving) - AuthController
 *
 * REST entry point for authentication.
 * Calls the UserInputPort (application use case).
 * Contains NO business logic — only HTTP translation.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Login and user registration")
public class AuthController {

    private final UserInputPort userInputPort;

    public AuthController(UserInputPort userInputPort) {
        this.userInputPort = userInputPort;
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate with identification number and password. Returns a JWT token.")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginCommand command) {
        LoginResponse response = userInputPort.login(command);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Register a new user. Roles: CLIENT_INDIVIDUAL, CLIENT_COMPANY, COMPANY_EMPLOYEE, COMPANY_SUPERVISOR")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterUserCommand command) {
        UserResponse response = userInputPort.registerUser(command);
        return ResponseEntity.status(201).body(ApiResponse.ok("User registered successfully", response));
    }
}
