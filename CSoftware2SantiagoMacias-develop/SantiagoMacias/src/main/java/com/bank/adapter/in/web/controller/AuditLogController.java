package com.bank.adapter.in.web.controller;

import com.bank.application.dto.BankingDto.*;
import com.bank.application.port.input.AuditLogInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ADAPTER (Driving) - AuditLogController
 * REST controller for read-only access to the immutable audit log.
 */
@RestController
@RequestMapping("/api/audit-log")
@Tag(name = "Audit Log", description = "Immutable operation log (NoSQL documents)")
@SecurityRequirement(name = "bearerAuth")
public class AuditLogController {

    private final AuditLogInputPort auditLogInputPort;

    public AuditLogController(AuditLogInputPort auditLogInputPort) {
        this.auditLogInputPort = auditLogInputPort;
    }

    @GetMapping
    @Operation(summary = "Get all audit log entries", description = "INTERNAL_ANALYST only")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAllLogs() {
        return ResponseEntity.ok(ApiResponse.ok("Audit log retrieved", auditLogInputPort.getAllLogs()));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get audit logs by affected product ID (account number, loan ID, transfer ID)")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getByProduct(@PathVariable String productId) {
        return ResponseEntity.ok(ApiResponse.ok("Logs retrieved", auditLogInputPort.getLogsByProductId(productId)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get audit logs by user ID", description = "INTERNAL_ANALYST only")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("Logs retrieved", auditLogInputPort.getLogsByUserId(userId)));
    }
}
