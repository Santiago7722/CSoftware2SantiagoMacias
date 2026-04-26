package com.bank.adapter.in.web.controller;

import com.bank.application.dto.BankingDto.ApiResponse;
import com.bank.application.dto.UserDto.*;
import com.bank.application.port.input.UserInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ADAPTER (Driving) - UserController
 *
 * REST controller for user management operations.
 * Requires JWT authentication.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserInputPort userInputPort;

    public UserController(UserInputPort userInputPort) {
        this.userInputPort = userInputPort;
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "INTERNAL_ANALYST only")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok("Users retrieved", userInputPort.getAllUsers()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("User found", userInputPort.getUserById(id)));
    }

    @GetMapping("/by-identification/{identificationNumber}")
    @Operation(summary = "Get user by identification number")
    public ResponseEntity<ApiResponse<UserResponse>> getByIdentification(@PathVariable String identificationNumber) {
        return ResponseEntity.ok(ApiResponse.ok("User found", userInputPort.getUserByIdentification(identificationNumber)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update user status", description = "INTERNAL_ANALYST only. Status: ACTIVE, INACTIVE, BLOCKED")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusCommand command) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated", userInputPort.updateUserStatus(id, command)));
    }
}
