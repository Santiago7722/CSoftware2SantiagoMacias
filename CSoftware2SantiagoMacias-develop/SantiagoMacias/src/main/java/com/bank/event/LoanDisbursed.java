package com.bank.event;
import com.bank.model.Money;
import java.time.LocalDateTime;
public record LoanDisbursed(int loanId, String clientId, Money disbursedAmount, String disbursementAccountNumber, int analystUserId, LocalDateTime occurredAt) implements DomainEvent {
    public LoanDisbursed(int loanId, String clientId, Money disbursedAmount, String disbursementAccountNumber, int analystUserId) { this(loanId, clientId, disbursedAmount, disbursementAccountNumber, analystUserId, LocalDateTime.now()); }
}
