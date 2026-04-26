package com.bank.event;
import com.bank.model.Money;
import java.time.LocalDateTime;
public record MoneyDeposited(String accountNumber, Money amount, Money balanceBefore, Money balanceAfter, LocalDateTime occurredAt) implements DomainEvent {
    public MoneyDeposited(String accountNumber, Money amount, Money balanceBefore, Money balanceAfter) { this(accountNumber, amount, balanceBefore, balanceAfter, LocalDateTime.now()); }
}
