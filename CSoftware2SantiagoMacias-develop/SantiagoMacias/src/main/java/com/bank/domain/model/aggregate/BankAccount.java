package com.bank.domain.model.aggregate;

import com.bank.domain.exception.DomainValidationException;
import com.bank.domain.exception.InsufficientFundsException;
import com.bank.domain.exception.AccountOperationNotAllowedException;
import com.bank.domain.model.valueobject.*;

import java.time.LocalDate;

/**
 * DOMAIN AGGREGATE ROOT - BankAccount
 *
 * Represents a bank account. This is the single entry point for all
 * balance-related operations. Business rules are enforced here.
 * No JPA or Spring dependencies.
 */
public class BankAccount {

    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private String ownerId;
    private Money balance;
    private AccountStatus status;
    private LocalDate openingDate;

    private BankAccount() {}

    /**
     * Reconstitution factory — restores a BankAccount from persistence.
     * Used ONLY by the persistence mapper.
     * @param id identificador único de la cuenta
     * @param accountNumber número de cuenta
     * @param accountType tipo de cuenta (SAVINGS, CHECKING)
     * @param ownerId identificación del propietario
     * @param balance saldo actual de la cuenta
     * @param status estado de la cuenta
     * @param openingDate fecha de apertura
     * @return la cuenta reconstruida
     */
    public static BankAccount reconstitute(Long id, String accountNumber, AccountType accountType,
                                            String ownerId, Money balance, AccountStatus status,
                                            java.time.LocalDate openingDate) {
        BankAccount account = new BankAccount();
        account.id = id;
        account.accountNumber = accountNumber;
        account.accountType = accountType;
        account.ownerId = ownerId;
        account.balance = balance;
        account.status = status;
        account.openingDate = openingDate;
        return account;
    }

    /**
     * Domain factory method — creates a new bank account with zero balance.
     * @param accountNumber número de cuenta
     * @param accountType tipo de cuenta (SAVINGS, CHECKING)
     * @param ownerId identificación del propietario
     * @param currency moneda de la cuenta
     * @return nueva cuenta creada
     */
    public static BankAccount open(String accountNumber, AccountType accountType,
                                    String ownerId, String currency) {
        if (accountNumber == null || accountNumber.isBlank())
            throw new DomainValidationException("Account number is required.");
        if (ownerId == null || ownerId.isBlank())
            throw new DomainValidationException("Owner ID is required.");
        if (currency == null || currency.isBlank())
            throw new DomainValidationException("Currency is required.");

        BankAccount account = new BankAccount();
        account.accountNumber = accountNumber.trim();
        account.accountType = accountType;
        account.ownerId = ownerId.trim();
        account.balance = Money.zero(currency);
        account.status = AccountStatus.ACTIVE;
        account.openingDate = LocalDate.now();
        return account;
    }

    // ============ Business Rules / Domain Operations ============

    public void deposit(Money amount) {
        assertOperational();
        if (amount.isZeroOrNegative())
            throw new DomainValidationException("Deposit amount must be greater than zero.");
        this.balance = this.balance.add(amount);
    }

    public void withdraw(Money amount) {
        assertOperational();
        if (amount.isZeroOrNegative())
            throw new DomainValidationException("Withdrawal amount must be greater than zero.");
        if (this.balance.isLessThan(amount))
            throw new InsufficientFundsException("Insufficient funds. Balance: " + this.balance + ", requested: " + amount);
        this.balance = this.balance.subtract(amount);
    }

    public void credit(Money amount) {
        // Used for receiving transfers or loan disbursements
        assertNotCancelled();
        if (amount.isZeroOrNegative())
            throw new DomainValidationException("Credit amount must be greater than zero.");
        this.balance = this.balance.add(amount);
    }

    public boolean hasSufficientFunds(Money amount) {
        return !this.balance.isLessThan(amount);
    }

    public void block() {
        if (this.status == AccountStatus.CANCELLED)
            throw new AccountOperationNotAllowedException("Cannot block a cancelled account.");
        this.status = AccountStatus.BLOCKED;
    }

    public void unblock() {
        if (this.status == AccountStatus.CANCELLED)
            throw new AccountOperationNotAllowedException("Cannot unblock a cancelled account.");
        this.status = AccountStatus.ACTIVE;
    }

    public void cancel() {
        this.status = AccountStatus.CANCELLED;
    }

    public boolean isActive() {
        return this.status == AccountStatus.ACTIVE;
    }

    public boolean isOperable() {
        return this.status == AccountStatus.ACTIVE;
    }

    private void assertOperational() {
        if (this.status != AccountStatus.ACTIVE)
            throw new AccountOperationNotAllowedException(
                    "Account " + accountNumber + " is " + status + ". Operations not allowed.");
    }

    private void assertNotCancelled() {
        if (this.status == AccountStatus.CANCELLED)
            throw new AccountOperationNotAllowedException("Account is cancelled.");
    }

    public boolean belongsTo(String ownerId) {
        return this.ownerId.equals(ownerId);
    }

    // ============ Getters & Setters ============

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public Money getBalance() { return balance; }
    public void setBalance(Money balance) { this.balance = balance; }
    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }
    public LocalDate getOpeningDate() { return openingDate; }
    public void setOpeningDate(LocalDate openingDate) { this.openingDate = openingDate; }
}
