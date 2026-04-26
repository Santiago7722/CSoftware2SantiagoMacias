package com.bank.adapter.out.persistence.mapper;

import com.bank.adapter.out.persistence.entity.BankAccountJpaEntity;
import com.bank.domain.model.aggregate.BankAccount;
import com.bank.domain.model.valueobject.Money;
import org.springframework.stereotype.Component;

/**
 * INFRASTRUCTURE MAPPER - BankAccountPersistenceMapper
 * Uses BankAccount.reconstitute() to restore from DB safely.
 */
@Component
public class BankAccountPersistenceMapper {

    public BankAccount toDomain(BankAccountJpaEntity e) {
        if (e == null) return null;
        return BankAccount.reconstitute(
            e.getId(),
            e.getAccountNumber(),
            e.getAccountType(),
            e.getOwnerId(),
            new Money(e.getBalance(), e.getCurrency()),
            e.getStatus(),
            e.getOpeningDate()
        );
    }

    public BankAccountJpaEntity toJpaEntity(BankAccount a) {
        if (a == null) return null;
        return BankAccountJpaEntity.builder()
            .id(a.getId())
            .accountNumber(a.getAccountNumber())
            .accountType(a.getAccountType())
            .ownerId(a.getOwnerId())
            .balance(a.getBalance().getAmount())
            .currency(a.getBalance().getCurrency())
            .status(a.getStatus())
            .openingDate(a.getOpeningDate())
            .build();
    }
}
