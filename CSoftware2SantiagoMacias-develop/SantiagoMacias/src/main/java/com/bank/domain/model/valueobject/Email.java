package com.bank.domain.model.valueobject;

import com.bank.domain.exception.InvalidEmailException;

import java.util.Objects;

/**
 * Value Object - Represents a validated email address.
 * Immutable by design (no setters, final fields).
 */
public final class Email {

    private final String value;

    public Email(String value) {
        validate(value);
        this.value = value.toLowerCase().trim();
    }

    private void validate(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidEmailException("Email is required.");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new InvalidEmailException("Email must contain '@' and a domain: " + email);
        }
        String[] parts = email.split("@");
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank() || !parts[1].contains(".")) {
            throw new InvalidEmailException("Invalid email format: " + email);
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
