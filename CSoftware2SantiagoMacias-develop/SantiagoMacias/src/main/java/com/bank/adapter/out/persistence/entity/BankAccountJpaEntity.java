package com.bank.adapter.out.persistence.entity;

import com.bank.domain.model.valueobject.AccountStatus;
import com.bank.domain.model.valueobject.AccountType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bank_accounts")
public class BankAccountJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(name = "balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(name = "opening_date", nullable = false)
    private LocalDate openingDate;

    public BankAccountJpaEntity() {}

    public Long getId()                  { return id; }
    public String getAccountNumber()     { return accountNumber; }
    public AccountType getAccountType()  { return accountType; }
    public String getOwnerId()           { return ownerId; }
    public BigDecimal getBalance()       { return balance; }
    public String getCurrency()          { return currency; }
    public AccountStatus getStatus()     { return status; }
    public LocalDate getOpeningDate()    { return openingDate; }

    public void setId(Long v)                  { this.id = v; }
    public void setAccountNumber(String v)     { this.accountNumber = v; }
    public void setAccountType(AccountType v)  { this.accountType = v; }
    public void setOwnerId(String v)           { this.ownerId = v; }
    public void setBalance(BigDecimal v)       { this.balance = v; }
    public void setCurrency(String v)          { this.currency = v; }
    public void setStatus(AccountStatus v)     { this.status = v; }
    public void setOpeningDate(LocalDate v)    { this.openingDate = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id;
        private String accountNumber, ownerId, currency;
        private AccountType accountType;
        private BigDecimal balance;
        private AccountStatus status;
        private LocalDate openingDate;
        public Builder id(Long v)                  { this.id = v; return this; }
        public Builder accountNumber(String v)     { this.accountNumber = v; return this; }
        public Builder accountType(AccountType v)  { this.accountType = v; return this; }
        public Builder ownerId(String v)           { this.ownerId = v; return this; }
        public Builder balance(BigDecimal v)       { this.balance = v; return this; }
        public Builder currency(String v)          { this.currency = v; return this; }
        public Builder status(AccountStatus v)     { this.status = v; return this; }
        public Builder openingDate(LocalDate v)    { this.openingDate = v; return this; }
        public BankAccountJpaEntity build() {
            BankAccountJpaEntity e = new BankAccountJpaEntity();
            e.id = id; e.accountNumber = accountNumber; e.accountType = accountType;
            e.ownerId = ownerId; e.balance = balance; e.currency = currency;
            e.status = status; e.openingDate = openingDate;
            return e;
        }
    }
}
