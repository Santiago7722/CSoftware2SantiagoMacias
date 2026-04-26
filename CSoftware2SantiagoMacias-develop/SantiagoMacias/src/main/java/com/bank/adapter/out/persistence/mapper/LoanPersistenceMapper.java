package com.bank.adapter.out.persistence.mapper;

import com.bank.adapter.out.persistence.entity.LoanJpaEntity;
import com.bank.domain.model.aggregate.Loan;
import com.bank.domain.model.valueobject.Money;
import org.springframework.stereotype.Component;

/**
 * INFRASTRUCTURE MAPPER - LoanPersistenceMapper
 * Uses Loan.reconstitute() to restore from DB — bypasses factory invariant checks
 * that are only relevant on creation, not on load.
 */
@Component
public class LoanPersistenceMapper {

    public Loan toDomain(LoanJpaEntity e) {
        if (e == null) return null;
        String currency = e.getCurrency() != null ? e.getCurrency() : "USD";
        Money requested = e.getRequestedAmount() != null ? new Money(e.getRequestedAmount(), currency) : null;
        Money approved  = e.getApprovedAmount()  != null ? new Money(e.getApprovedAmount(),  currency) : null;
        return Loan.reconstitute(
            e.getId(), e.getClientId(), e.getLoanType(),
            requested, approved, e.getInterestRate(), e.getTermMonths(),
            e.getStatus(), e.getApprovalDate(), e.getDisbursementDate(),
            e.getDisbursementAccount(), e.getCreatorUserId(), e.getAnalystUserId()
        );
    }

    public LoanJpaEntity toJpaEntity(Loan l) {
        if (l == null) return null;
        String currency = l.getRequestedAmount() != null ? l.getRequestedAmount().getCurrency() : "USD";
        return LoanJpaEntity.builder()
            .id(l.getId())
            .loanType(l.getLoanType())
            .clientId(l.getClientId())
            .requestedAmount(l.getRequestedAmount() != null ? l.getRequestedAmount().getAmount() : null)
            .approvedAmount(l.getApprovedAmount()   != null ? l.getApprovedAmount().getAmount()  : null)
            .interestRate(l.getInterestRate())
            .termMonths(l.getTermMonths())
            .status(l.getStatus())
            .approvalDate(l.getApprovalDate())
            .disbursementDate(l.getDisbursementDate())
            .disbursementAccount(l.getDisbursementAccountNumber())
            .currency(currency)
            .creatorUserId(l.getCreatorUserId())
            .analystUserId(l.getAnalystUserId())
            .build();
    }
}
