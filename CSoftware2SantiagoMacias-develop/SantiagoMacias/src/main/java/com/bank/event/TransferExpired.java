package com.bank.event;
import java.time.LocalDateTime;
public record TransferExpired(int transferId, long minutesPending, LocalDateTime occurredAt) implements DomainEvent {
    public TransferExpired(int transferId, long minutesPending) { this(transferId, minutesPending, LocalDateTime.now()); }
}
