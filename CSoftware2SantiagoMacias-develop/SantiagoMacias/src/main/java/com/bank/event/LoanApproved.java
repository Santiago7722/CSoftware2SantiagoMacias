package com.bank.event;
import com.bank.model.Money;
import java.time.LocalDateTime;
public record LoanApproved(int loanId, String clientId, Money approvedAmount, double interestRate, int termMonths, int analystUserId, LocalDateTime occurredAt) implements DomainEvent {
    public LoanApproved(int loanId, String clientId, Money approvedAmount, double interestRate, int termMonths, int analystUserId) { this(loanId, clientId, approvedAmount, interestRate, termMonths, analystUserId, LocalDateTime.now()); }
}
