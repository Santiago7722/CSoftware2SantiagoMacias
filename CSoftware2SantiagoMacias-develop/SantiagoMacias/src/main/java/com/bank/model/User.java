package com.bank.model;

import java.time.LocalDate;

/**
 * ENTIDAD — Usuario del sistema bancario (DDD).
 */
public class User {

    private int userId;
    private String relatedEntityId;
    private String fullName;
    private String identificationNumber;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String address;
    private UserRole role;
    private UserStatus status;
    private String passwordHash;
    private String companyId;

    public User() {}

    // -------------------------------------------------------------------------
    // Comportamiento de dominio
    // -------------------------------------------------------------------------

    /** Verifica si el usuario puede operar (está ACTIVO). */
    public boolean isOperational() {
        return this.status == UserStatus.ACTIVE;
    }

    /** Bloquea el usuario. */
    public void block() {
        if (this.status == UserStatus.INACTIVE)
            throw new DomainException("Cannot block an inactive user.");
        this.status = UserStatus.BLOCKED;
    }

    /** Activa el usuario. */
    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    /** Verifica si tiene alguno de los roles dados. */
    public boolean hasRole(UserRole... allowedRoles) {
        for (UserRole r : allowedRoles) {
            if (this.role == r) return true;
        }
        return false;
    }

    /** Verifica si este usuario es dueño de la identificación dada. */
    public boolean isOwner(String identificationNumber) {
        return this.identificationNumber != null && this.identificationNumber.equals(identificationNumber);
    }

    // -------------------------------------------------------------------------
    // Getters y setters
    // -------------------------------------------------------------------------

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(String relatedEntityId) { this.relatedEntityId = relatedEntityId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getIdentificationNumber() { return identificationNumber; }
    public void setIdentificationNumber(String identificationNumber) { this.identificationNumber = identificationNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

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
        return String.format("User[id=%d, name=%s, role=%s, status=%s]", userId, fullName, role, status);
    }
}
