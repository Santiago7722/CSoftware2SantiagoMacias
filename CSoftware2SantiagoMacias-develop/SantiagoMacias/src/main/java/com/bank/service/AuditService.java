package com.bank.service;

import com.bank.model.AuditLog;
import com.bank.model.UserRole;
import com.bank.repository.AuditLogRepository;

import java.util.List;

public class AuditService {

    private final AuditLogRepository auditRepo;

    public AuditService() {
        this.auditRepo = new AuditLogRepository();
    }

    public List<AuditLog> getAllLogs() {
        AuthService.requireRole(UserRole.INTERNAL_ANALYST);
        return auditRepo.findAll();
    }

    public List<AuditLog> getLogsByProduct(String productId) {
        AuthService.requireLogin();
        return auditRepo.findByProductId(productId);
    }

    public List<AuditLog> getLogsByUser(int userId) {
        AuthService.requireRole(UserRole.INTERNAL_ANALYST);
        return auditRepo.findByUserId(userId);
    }
}
