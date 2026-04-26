package com.bank.event;
import com.bank.model.AccountType;
import java.time.LocalDateTime;
public record AccountOpened(String accountNumber, String ownerId, AccountType accountType, String currency, LocalDateTime occurredAt) implements DomainEvent {
    public AccountOpened(String accountNumber, String ownerId, AccountType accountType, String currency) { this(accountNumber, ownerId, accountType, currency, LocalDateTime.now()); }
}
