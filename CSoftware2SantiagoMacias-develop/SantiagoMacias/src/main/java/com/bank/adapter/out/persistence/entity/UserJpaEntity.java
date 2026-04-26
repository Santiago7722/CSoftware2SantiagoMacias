package com.bank.adapter.out.persistence.entity;

import com.bank.domain.model.valueobject.UserRole;
import com.bank.domain.model.valueobject.UserStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "related_entity_id")
    private String relatedEntityId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "identification_number", nullable = false, unique = true)
    private String identificationNumber;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "company_id")
    private String companyId;

    public UserJpaEntity() {}

    public Long getId()                      { return id; }
    public String getRelatedEntityId()       { return relatedEntityId; }
    public String getFullName()              { return fullName; }
    public String getIdentificationNumber()  { return identificationNumber; }
    public String getEmail()                 { return email; }
    public String getPhone()                 { return phone; }
    public LocalDate getBirthDate()          { return birthDate; }
    public String getAddress()               { return address; }
    public UserRole getRole()                { return role; }
    public UserStatus getStatus()            { return status; }
    public String getPasswordHash()          { return passwordHash; }
    public String getCompanyId()             { return companyId; }

    public void setId(Long v)                      { this.id = v; }
    public void setRelatedEntityId(String v)       { this.relatedEntityId = v; }
    public void setFullName(String v)              { this.fullName = v; }
    public void setIdentificationNumber(String v)  { this.identificationNumber = v; }
    public void setEmail(String v)                 { this.email = v; }
    public void setPhone(String v)                 { this.phone = v; }
    public void setBirthDate(LocalDate v)          { this.birthDate = v; }
    public void setAddress(String v)               { this.address = v; }
    public void setRole(UserRole v)                { this.role = v; }
    public void setStatus(UserStatus v)            { this.status = v; }
    public void setPasswordHash(String v)          { this.passwordHash = v; }
    public void setCompanyId(String v)             { this.companyId = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id;
        private String relatedEntityId, fullName, identificationNumber, email, phone, address, passwordHash, companyId;
        private LocalDate birthDate;
        private UserRole role;
        private UserStatus status;
        public Builder id(Long v)                      { this.id = v; return this; }
        public Builder relatedEntityId(String v)       { this.relatedEntityId = v; return this; }
        public Builder fullName(String v)              { this.fullName = v; return this; }
        public Builder identificationNumber(String v)  { this.identificationNumber = v; return this; }
        public Builder email(String v)                 { this.email = v; return this; }
        public Builder phone(String v)                 { this.phone = v; return this; }
        public Builder birthDate(LocalDate v)          { this.birthDate = v; return this; }
        public Builder address(String v)               { this.address = v; return this; }
        public Builder role(UserRole v)                { this.role = v; return this; }
        public Builder status(UserStatus v)            { this.status = v; return this; }
        public Builder passwordHash(String v)          { this.passwordHash = v; return this; }
        public Builder companyId(String v)             { this.companyId = v; return this; }
        public UserJpaEntity build() {
            UserJpaEntity e = new UserJpaEntity();
            e.id = id; e.relatedEntityId = relatedEntityId; e.fullName = fullName;
            e.identificationNumber = identificationNumber; e.email = email; e.phone = phone;
            e.birthDate = birthDate; e.address = address; e.role = role;
            e.status = status; e.passwordHash = passwordHash; e.companyId = companyId;
            return e;
        }
    }
}
