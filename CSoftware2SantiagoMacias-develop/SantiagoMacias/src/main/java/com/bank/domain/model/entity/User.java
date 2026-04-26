package com.bank.domain.model.entity;

import com.bank.domain.exception.DomainValidationException;
import com.bank.domain.model.valueobject.*;

import java.time.LocalDate;
import java.time.Period;

/**
 * DOMAIN ENTITY - User
 *
 * Represents any user of the system (client individual, company, employee, etc.).
 * This is the core domain entity — it contains identity and business rules.
 * It has NO dependency on Spring, JPA, or any framework.
 */
public class User {

    private Long id;
    private String relatedEntityId;
    private String fullName;
    private String identificationNumber;
    private Email email;
    private PhoneNumber phone;
    private LocalDate birthDate;
    private String address;
    private UserRole role;
    private UserStatus status;
    private String passwordHash;
    private String companyId;

    // Private constructor — use factory method or builder
    private User() {}

    /**
     * Reconstitution factory — restores a User from persistence WITHOUT re-running invariants.
     * Used ONLY by the persistence mapper.
     */
    public static User reconstitute(Long id, String relatedEntityId, String fullName,
                                     String identificationNumber, String email, String phone,
                                     java.time.LocalDate birthDate, String address,
                                     UserRole role, UserStatus status,
                                     String passwordHash, String companyId) {
        User user = new User();
        user.id = id;
        user.relatedEntityId = relatedEntityId;
        user.fullName = fullName;
        user.identificationNumber = identificationNumber;
        user.email = new com.bank.domain.model.valueobject.Email(email);
        user.phone = new com.bank.domain.model.valueobject.PhoneNumber(phone);
        user.birthDate = birthDate;
        user.address = address;
        user.role = role;
        user.status = status;
        user.passwordHash = passwordHash;
        user.companyId = companyId;
        return user;
    }

    /**
     * Domain factory method — enforces all business invariants on creation.
     */
    public static User create(String fullName, String identificationNumber,
                               String email, String phone, LocalDate birthDate,
                               String address, UserRole role) {
        User user = new User();
        user.setFullName(fullName);
        user.setIdentificationNumber(identificationNumber);
        user.email = new Email(email);
        user.phone = new PhoneNumber(phone);
        user.setBirthDate(birthDate, role);
        user.setAddress(address);
        user.role = role;
        user.status = UserStatus.ACTIVE;
        return user;
    }

    // ============ Business Rules ============

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    public boolean isBlocked() {
        return this.status == UserStatus.BLOCKED;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public void block() {
        this.status = UserStatus.BLOCKED;
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    public boolean isAtLeast18YearsOld() {
        if (birthDate == null) return false;
        return Period.between(birthDate, LocalDate.now()).getYears() >= 18;
    }

    public boolean isClientRole() {
        return role == UserRole.CLIENT_INDIVIDUAL || role == UserRole.CLIENT_COMPANY;
    }

    public boolean isBankEmployee() {
        return role == UserRole.TELLER || role == UserRole.COMMERCIAL_EMPLOYEE || role == UserRole.INTERNAL_ANALYST;
    }

    public boolean isCompanyUser() {
        return role == UserRole.COMPANY_EMPLOYEE || role == UserRole.COMPANY_SUPERVISOR;
    }

    // ============ Setters with validation ============

    public void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank())
            throw new DomainValidationException("Full name is required.");
        this.fullName = fullName.trim();
    }

    public void setIdentificationNumber(String idNumber) {
        if (idNumber == null || idNumber.isBlank())
            throw new DomainValidationException("Identification number is required.");
        this.identificationNumber = idNumber.trim();
    }

    public void setEmail(String email) {
        this.email = new Email(email);
    }

    public void setPhone(String phone) {
        this.phone = new PhoneNumber(phone);
    }

    public void setBirthDate(LocalDate birthDate, UserRole role) {
        if (role == UserRole.CLIENT_INDIVIDUAL) {
            if (birthDate == null) throw new DomainValidationException("Birth date is required for individual clients.");
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            if (age < 18) throw new DomainValidationException("Client must be at least 18 years old. Age: " + age);
        }
        this.birthDate = birthDate;
    }

    public void setAddress(String address) {
        if (address == null || address.isBlank())
            throw new DomainValidationException("Address is required.");
        this.address = address.trim();
    }

    // ============ Getters ============

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(String relatedEntityId) { this.relatedEntityId = relatedEntityId; }
    public String getFullName() { return fullName; }
    public String getIdentificationNumber() { return identificationNumber; }
    public Email getEmail() { return email; }
    public PhoneNumber getPhone() { return phone; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getAddress() { return address; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name=" + fullName + ", role=" + role + ", status=" + status + "}";
    }
}
