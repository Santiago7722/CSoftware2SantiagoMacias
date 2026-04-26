package com.bank.adapter.out.persistence;

import com.bank.adapter.out.persistence.mapper.AuditLogPersistenceMapper;
import com.bank.adapter.out.persistence.repository.AuditLogJpaRepository;
import com.bank.application.port.output.AuditLogRepositoryPort;
import com.bank.domain.model.entity.AuditLog;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * INFRASTRUCTURE ADAPTER - AuditLogPersistenceAdapter
 * Implements AuditLogRepositoryPort — stores JSON documents in a relational DB.
 */
@Component
public class AuditLogPersistenceAdapter implements AuditLogRepositoryPort {

    private final AuditLogJpaRepository jpaRepository;
    private final AuditLogPersistenceMapper mapper;

    public AuditLogPersistenceAdapter(AuditLogJpaRepository jpaRepository, AuditLogPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public AuditLog save(AuditLog auditLog) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(auditLog)));
    }

    @Override
    public List<AuditLog> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AuditLog> findByAffectedProductId(String productId) {
        return jpaRepository.findByAffectedProductId(productId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AuditLog> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream().map(mapper::toDomain).toList();
    }
}
