package com.bank.domain.model.valueobject;

import com.bank.domain.exception.DomainValidationException;

import java.util.Objects;

/**
 * Value Object - Represents a validated phone number.
 * Immutable, validated on construction.
 */
public final class PhoneNumber {

    private final String value;

    public PhoneNumber(String value) {
        validate(value);
        this.value = value.trim().replaceAll("[^0-9+]", "");
    }

    private void validate(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new DomainValidationException("Phone number is required.");
        }
        String digits = phone.trim().replaceAll("[^0-9+]", "");
        if (digits.length() < 7 || digits.length() > 15) {
            throw new DomainValidationException("Phone number must be between 7 and 15 digits: " + phone);
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhoneNumber that)) return false;
        return Objects.equals(value, that.value);
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
