package com.bank.domain.model.valueobject;

/**
 * Value Object - Represents the role of a user in the system.
 * Defines what operations each user can perform.
 */
public enum UserRole {
    CLIENT_INDIVIDUAL("Client - Individual Person"),
    CLIENT_COMPANY("Client - Company Representative"),
    TELLER("Teller / Service Advisor"),
    COMMERCIAL_EMPLOYEE("Commercial Employee"),
    COMPANY_EMPLOYEE("Company Employee (Operative)"),
    COMPANY_SUPERVISOR("Company Supervisor (Approver)"),
    INTERNAL_ANALYST("Internal Analyst (Risk/Compliance)");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
