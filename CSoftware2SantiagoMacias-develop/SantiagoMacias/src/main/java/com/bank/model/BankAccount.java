package com.bank.model;

import com.bank.event.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AGGREGATE ROOT — Cuenta Bancaria (DDD).
 *
 * Encapsula todas las reglas de negocio de una cuenta.
 * Nadie puede modificar el saldo sin pasar por sus métodos.
 */
public class BankAccount {

    private String accountNumber;
    private AccountType accountType;
    private String ownerId;
    private Money balance;
    private AccountStatus status;
    private LocalDate openingDate;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public BankAccount() {}

    /** Factory method — crea una cuenta nueva con sus reglas aplicadas. */
    public static BankAccount open(String accountNumber, String ownerId,
                                   AccountType accountType, String currency) {
        if (accountNumber == null || accountNumber.isBlank())
            throw new DomainException("Account number is required.");
        if (ownerId == null || ownerId.isBlank())
            throw new DomainException("Owner ID is required.");
        if (accountType == null)
            throw new DomainException("Account type is required.");

        BankAccount account = new BankAccount();
        account.accountNumber = accountNumber;
        account.ownerId = ownerId;
        account.accountType = accountType;
        account.balance = Money.zero(currency != null ? currency : "USD");
        account.status = AccountStatus.ACTIVE;
        account.openingDate = LocalDate.now();

        account.domainEvents.add(new AccountOpened(accountNumber, ownerId, accountType, account.balance.getCurrency()));
        return account;
    }

    // -------------------------------------------------------------------------
    // Comportamiento de dominio
    // -------------------------------------------------------------------------

    /** Deposita dinero. Regla: cuenta debe estar ACTIVA. */
    public void deposit(Money amount) {
        validateActive();
        validatePositiveAmount(amount);
        Money before = this.balance;
        this.balance = this.balance.add(amount);
        domainEvents.add(new MoneyDeposited(accountNumber, amount, before, this.balance));
    }

    /** Retira dinero. Reglas: cuenta ACTIVA y saldo suficiente. */
    public void withdraw(Money amount) {
        validateActive();
        validatePositiveAmount(amount);
        if (this.balance.isLessThan(amount))
            throw new DomainException("Insufficient funds. Available: " + balance + ", requested: " + amount);
        Money before = this.balance;
        this.balance = this.balance.subtract(amount);
        domainEvents.add(new MoneyWithdrawn(accountNumber, amount, before, this.balance));
    }

    /** Bloquea la cuenta. Regla: no puede estar CANCELADA. */
    public void block(String reason) {
        if (this.status == AccountStatus.CANCELLED)
            throw new DomainException("Cannot block a cancelled account.");
        this.status = AccountStatus.BLOCKED;
        domainEvents.add(new AccountBlocked(accountNumber, reason));
    }

    /** Reactiva una cuenta bloqueada. */
    public void activate() {
        if (this.status == AccountStatus.CANCELLED)
            throw new DomainException("Cannot activate a cancelled account.");
        this.status = AccountStatus.ACTIVE;
    }

    // -------------------------------------------------------------------------
    // Validaciones privadas
    // -------------------------------------------------------------------------

    private void validateActive() {
        if (this.status != AccountStatus.ACTIVE)
            throw new DomainException("Account " + accountNumber + " is " + status + ". Cannot perform operations.");
    }

    private void validatePositiveAmount(Money amount) {
        if (amount == null || amount.isZero())
            throw new DomainException("Amount must be greater than zero.");
    }

    // -------------------------------------------------------------------------
    // Domain Events
    // -------------------------------------------------------------------------

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return Collections.unmodifiableList(events);
    }

    // -------------------------------------------------------------------------
    // Getters (sin setters públicos para atributos críticos)
    // -------------------------------------------------------------------------

    public String getAccountNumber() { return accountNumber; }
    public AccountType getAccountType() { return accountType; }
    public String getOwnerId() { return ownerId; }
    public Money getBalance() { return balance; }
    public AccountStatus getStatus() { return status; }
    public LocalDate getOpeningDate() { return openingDate; }

    // Compatibilidad con ConsoleUI que usa getCurrentBalance() y getCurrency()
    public java.math.BigDecimal getCurrentBalance() { return balance != null ? balance.getAmount() : java.math.BigDecimal.ZERO; }
    public String getCurrency() { return balance != null ? balance.getCurrency() : "USD"; }

    // Setters para reconstitución desde BD
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public void setBalance(Money balance) { this.balance = balance; }
    public void setStatus(AccountStatus status) { this.status = status; }
    public void setOpeningDate(LocalDate openingDate) { this.openingDate = openingDate; }

    @Override
    public String toString() {
        return String.format("BankAccount[#%s, type=%s, balance=%s, status=%s]",
                accountNumber, accountType, balance, status);
    }
}
