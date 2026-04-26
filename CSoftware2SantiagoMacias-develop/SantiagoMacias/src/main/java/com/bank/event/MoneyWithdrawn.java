package com.bank.event;
import com.bank.model.Money;
import java.time.LocalDateTime;
public record MoneyWithdrawn(String accountNumber, Money amount, Money balanceBefore, Money balanceAfter, LocalDateTime occurredAt) implements DomainEvent {
    public MoneyWithdrawn(String accountNumber, Money amount, Money balanceBefore, Money balanceAfter) { this(accountNumber, amount, balanceBefore, balanceAfter, LocalDateTime.now()); }
}
