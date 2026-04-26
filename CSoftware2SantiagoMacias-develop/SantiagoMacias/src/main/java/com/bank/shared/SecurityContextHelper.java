package com.bank.shared;

import com.bank.domain.exception.AccessDeniedException;
import com.bank.domain.model.entity.User;
import com.bank.domain.model.valueobject.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;

/**
 * SHARED UTILITY - SecurityContextHelper
 *
 * Provides convenient access to the authenticated user from the Spring Security context.
 * Used by use cases to enforce role-based access control.
 */
public class SecurityContextHelper {

    private SecurityContextHelper() {}

    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof User user) return user;
        return null;
    }

    public static void requireLogin() {
        if (getCurrentUser() == null)
            throw new AccessDeniedException("You must be authenticated to perform this operation.");
    }

    public static void requireAnyRole(UserRole... allowedRoles) {
        requireLogin();
        User current = getCurrentUser();
        boolean hasRole = Arrays.stream(allowedRoles)
            .anyMatch(role -> role == current.getRole());
        if (!hasRole) {
            throw new AccessDeniedException(
                "Access denied. Your role '" + current.getRole().name()
                + "' is not authorized. Required: " + Arrays.toString(allowedRoles));
        }
    }
}
