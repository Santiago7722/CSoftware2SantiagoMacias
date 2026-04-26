package com.bank.event;
import java.time.LocalDateTime;
public record AccountBlocked(String accountNumber, String reason, LocalDateTime occurredAt) implements DomainEvent {
    public AccountBlocked(String accountNumber, String reason) { this(accountNumber, reason, LocalDateTime.now()); }
}
