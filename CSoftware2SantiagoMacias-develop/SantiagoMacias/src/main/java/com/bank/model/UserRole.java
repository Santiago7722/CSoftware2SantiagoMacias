package com.bank.model;

public enum UserRole {
    TELLER("Teller"),
    INTERNAL_ANALYST("Internal Analyst"),
    COMMERCIAL_EMPLOYEE("Commercial Employee"),
    COMPANY_SUPERVISOR("Company Supervisor"),
    COMPANY_EMPLOYEE("Company Employee"),
    CLIENT_INDIVIDUAL("Individual Client"),
    CLIENT_COMPANY("Company Client");

    private final String displayName;
    UserRole(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
}
