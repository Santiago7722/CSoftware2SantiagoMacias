package com.bank.domain.model.valueobject;

/**
 * Value Object - Represents the type of a bank account.
 */
public enum AccountType {
    SAVINGS("Savings"),
    CHECKING("Checking"),
    PERSONAL("Personal"),
    BUSINESS("Business");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
