package com.bank.application.port.output;

import com.bank.domain.model.entity.AuditLog;
import java.util.List;

/**
 * APPLICATION OUTPUT PORT - AuditLogRepositoryPort
 * Abstracts persistence for AuditLog (NoSQL document store).
 */
public interface AuditLogRepositoryPort {
    AuditLog save(AuditLog auditLog);
    List<AuditLog> findAll();
    List<AuditLog> findByAffectedProductId(String productId);
    List<AuditLog> findByUserId(Long userId);
}
