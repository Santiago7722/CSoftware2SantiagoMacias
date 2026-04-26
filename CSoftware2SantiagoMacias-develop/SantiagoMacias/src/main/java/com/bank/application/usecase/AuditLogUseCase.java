package com.bank.application.usecase;

import com.bank.application.dto.BankingDto.AuditLogResponse;
import com.bank.application.port.input.AuditLogInputPort;
import com.bank.application.port.output.AuditLogRepositoryPort;
import com.bank.domain.model.entity.AuditLog;
import com.bank.domain.model.valueobject.UserRole;
import com.bank.shared.SecurityContextHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * APPLICATION USE CASE - AuditLogUseCase
 *
 * Read-only access to the immutable audit log.
 * Only INTERNAL_ANALYST can see the complete log.
 */
@Service
@Transactional(readOnly = true)
public class AuditLogUseCase implements AuditLogInputPort {

    private final AuditLogRepositoryPort auditLogRepository;

    public AuditLogUseCase(AuditLogRepositoryPort auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public List<AuditLogResponse> getAllLogs() {
        SecurityContextHelper.requireAnyRole(UserRole.INTERNAL_ANALYST);
        return auditLogRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<AuditLogResponse> getLogsByProductId(String productId) {
        SecurityContextHelper.requireLogin();
        return auditLogRepository.findByAffectedProductId(productId)
            .stream().map(this::toResponse).toList();
    }

    @Override
    public List<AuditLogResponse> getLogsByUserId(Long userId) {
        SecurityContextHelper.requireAnyRole(UserRole.INTERNAL_ANALYST);
        return auditLogRepository.findByUserId(userId)
            .stream().map(this::toResponse).toList();
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return new AuditLogResponse(
            log.getLogId(), log.getOperationType(), log.getOperationDateTime(),
            log.getUserId(), log.getUserRole(), log.getAffectedProductId(), log.getDetailData()
        );
    }
}
