package com.bank.event;
import java.time.LocalDateTime;
public record LoanRejected(int loanId, String clientId, String reason, int analystUserId, LocalDateTime occurredAt) implements DomainEvent {
    public LoanRejected(int loanId, String clientId, String reason, int analystUserId) { this(loanId, clientId, reason, analystUserId, LocalDateTime.now()); }
}
