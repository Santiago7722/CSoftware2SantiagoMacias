package com.bank.adapter.out.persistence.mapper;

import com.bank.adapter.out.persistence.entity.TransferJpaEntity;
import com.bank.domain.model.aggregate.Transfer;
import com.bank.domain.model.valueobject.Money;
import org.springframework.stereotype.Component;

/**
 * INFRASTRUCTURE MAPPER - TransferPersistenceMapper
 * Uses Transfer.reconstitute() to restore from DB — preserves any persisted status.
 */
@Component
public class TransferPersistenceMapper {

    public Transfer toDomain(TransferJpaEntity e) {
        if (e == null) return null;
        return Transfer.reconstitute(
            e.getId(),
            e.getSourceAccount(),
            e.getDestinationAccount(),
            new Money(e.getAmount(), e.getCurrency()),
            e.getStatus(),
            e.getCreationDateTime(),
            e.getApprovalDateTime(),
            e.getCreatorUserId(),
            e.getApproverUserId()
        );
    }

    public TransferJpaEntity toJpaEntity(Transfer t) {
        if (t == null) return null;
        return TransferJpaEntity.builder()
            .id(t.getId())
            .sourceAccount(t.getSourceAccount())
            .destinationAccount(t.getDestinationAccount())
            .amount(t.getAmount().getAmount())
            .currency(t.getAmount().getCurrency())
            .creationDateTime(t.getCreationDateTime())
            .approvalDateTime(t.getApprovalDateTime())
            .status(t.getStatus())
            .creatorUserId(t.getCreatorUserId())
            .approverUserId(t.getApproverUserId())
            .build();
    }
}
