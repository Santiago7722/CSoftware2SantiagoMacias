package com.bank.event;
import com.bank.model.Money;
import java.time.LocalDateTime;
public record TransferExecuted(int transferId, String sourceAccount, String destinationAccount, Money amount, int creatorUserId, LocalDateTime occurredAt) implements DomainEvent {
    public TransferExecuted(int transferId, String sourceAccount, String destinationAccount, Money amount, int creatorUserId) { this(transferId, sourceAccount, destinationAccount, amount, creatorUserId, LocalDateTime.now()); }
}
