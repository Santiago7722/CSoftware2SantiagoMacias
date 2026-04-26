package com.bank.application.port.input;

import com.bank.application.dto.BankingDto.*;
import java.util.List;

/**
 * APPLICATION INPUT PORT - AuditLogInputPort
 * Defines read operations on the immutable audit log.
 */
public interface AuditLogInputPort {
    List<AuditLogResponse> getAllLogs();
    List<AuditLogResponse> getLogsByProductId(String productId);
    List<AuditLogResponse> getLogsByUserId(Long userId);
}
