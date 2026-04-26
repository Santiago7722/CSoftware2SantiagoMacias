package com.bank.domain.repository;

import com.bank.domain.model.entity.AuditLog;

import java.util.List;

/**
 * DOMAIN REPOSITORY INTERFACE (Output Port at Domain Level)
 * Defines persistence operations for AuditLog (NoSQL-like).
 */
public interface AuditLogRepository {
    AuditLog save(AuditLog auditLog);
    List<AuditLog> findAll();
    List<AuditLog> findByAffectedProductId(String productId);
    List<AuditLog> findByUserId(Long userId);
}
