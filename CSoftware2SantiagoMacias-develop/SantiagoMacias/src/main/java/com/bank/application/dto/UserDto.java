package com.bank.application.dto;

import com.bank.domain.model.valueobject.UserRole;
import com.bank.domain.model.valueobject.UserStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * APPLICATION LAYER - DTOs
 * Simple data containers to carry data between layers.
 * They shield the domain from external formats (JSON, HTTP, etc.).
 */
public class UserDto {

    // ─── Commands (Input) ───────────────────────────────────────────────

    public record RegisterUserCommand(
        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Identification number is required")
        String identificationNumber,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Phone is required")
        @Size(min = 7, max = 15, message = "Phone must be between 7 and 15 characters")
        String phone,

        LocalDate birthDate,

        @NotBlank(message = "Address is required")
        String address,

        @NotNull(message = "Role is required")
        UserRole role,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        String companyId
    ) {}

    public record UpdateUserStatusCommand(
        @NotNull(message = "Status is required")
        UserStatus status
    ) {}

    // ─── Responses (Output) ─────────────────────────────────────────────

    public record UserResponse(
        Long id,
        String fullName,
        String identificationNumber,
        String email,
        String phone,
        LocalDate birthDate,
        String address,
        UserRole role,
        UserStatus status,
        String companyId
    ) {}

    // ─── Auth ────────────────────────────────────────────────────────────

    public record LoginCommand(
        @NotBlank(message = "Identification number is required")
        String identificationNumber,

        @NotBlank(message = "Password is required")
        String password
    ) {}

    public record LoginResponse(
        String token,
        String tokenType,
        Long userId,
        String fullName,
        UserRole role
    ) {}
}
