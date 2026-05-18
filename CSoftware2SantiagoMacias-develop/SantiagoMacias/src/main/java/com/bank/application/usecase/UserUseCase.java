package com.bank.application.usecase;

import com.bank.application.dto.UserDto.*;
import com.bank.application.port.input.UserInputPort;
import com.bank.application.port.output.UserRepositoryPort;
import com.bank.domain.exception.AccessDeniedException;
import com.bank.domain.exception.DomainValidationException;
import com.bank.domain.exception.ResourceNotFoundException;
import com.bank.domain.model.entity.User;
import com.bank.domain.model.valueobject.UserRole;
import com.bank.shared.SecurityContextHelper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

/**
 * APPLICATION USE CASE - UserUseCase
 *
 * Orchestrates user-related operations.
 * Implements the UserInputPort (driven by REST controllers).
 * Uses UserRepositoryPort (implemented by JPA adapter).
 *
 * RULE: Contains NO business logic — only orchestration.
 * Business rules live in the domain entities.
 */
@Service
@Transactional
public class UserUseCase implements UserInputPort {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    public UserUseCase(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse login(LoginCommand command) {
        User user = userRepository.findByIdentificationNumber(command.identificationNumber())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + command.identificationNumber()));

        if (!user.isActive())
            throw new AccessDeniedException("Account is " + user.getStatus() + ". Contact the administrator.");

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash()))
            throw new AccessDeniedException("Invalid credentials.");

        String token = generateJwtToken(user);
        return new LoginResponse(token, "Bearer", user.getId(), user.getFullName(), user.getRole());
    }

    @Override
    public UserResponse registerUser(RegisterUserCommand command) {
        if (userRepository.existsByIdentificationNumber(command.identificationNumber()))
            throw new DomainValidationException("Identification number already exists: " + command.identificationNumber());

        User user = User.create(
            command.fullName(),
            command.identificationNumber(),
            command.email(),
            command.phone(),
            command.birthDate(),
            command.address(),
            command.role()
        );
        user.setPasswordHash(passwordEncoder.encode(command.password()));
        user.setCompanyId(command.companyId());

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        assertCanViewUser(user);
        return toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByIdentification(String identificationNumber) {
        User user = userRepository.findByIdentificationNumber(identificationNumber)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + identificationNumber));
        assertCanViewUser(user);
        return toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        SecurityContextHelper.requireAnyRole(UserRole.INTERNAL_ANALYST);
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public UserResponse updateUserStatus(Long userId, UpdateUserStatusCommand command) {
        SecurityContextHelper.requireAnyRole(UserRole.INTERNAL_ANALYST);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        user.setStatus(command.status());
        return toResponse(userRepository.save(user));
    }

    // ─── Private Helpers ────────────────────────────────────────────────

    private void assertCanViewUser(User targetUser) {
        User current = SecurityContextHelper.getCurrentUser();
        if (current == null) return;
        if (current.getRole() == UserRole.CLIENT_INDIVIDUAL || current.getRole() == UserRole.CLIENT_COMPANY) {
            if (!current.getId().equals(targetUser.getId()))
                throw new AccessDeniedException("Clients can only view their own profile.");
        }
    }

    private String generateJwtToken(User user) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
            .subject(user.getIdentificationNumber())
            .claim("userId", user.getId())
            .claim("role", user.getRole().name())
            .claim("fullName", user.getFullName())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(key)
            .compact();
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(
            u.getId(), u.getFullName(), u.getIdentificationNumber(),
            u.getEmail().getValue(), u.getPhone().getValue(),
            u.getBirthDate(), u.getAddress(), u.getRole(), u.getStatus(), u.getCompanyId()
        );
    }

}
