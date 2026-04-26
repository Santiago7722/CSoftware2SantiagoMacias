package com.bank.repository;

import com.bank.config.DatabaseManager;
import com.bank.event.DomainEvent;
import com.bank.event.*;
import com.bank.model.AuditLog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Repositorio de auditoría.
 * Implementa DomainEventPublisher para convertir Domain Events en registros de auditoría.
 */
public class AuditLogRepository implements DomainEventPublisher {

    private final Connection conn;
    private final ObjectMapper objectMapper;

    public AuditLogRepository() {
        this.conn = DatabaseManager.getInstance().getConnection();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // ── DomainEventPublisher ──────────────────────────────────────────────────

    @Override
    public void publish(DomainEvent event) {
        if (event instanceof MoneyDeposited e) {
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("amount", e.amount().toString());
            d.put("balanceBefore", e.balanceBefore().toString());
            d.put("balanceAfter", e.balanceAfter().toString());
            saveRaw("DEPOSIT", e.accountNumber(), d, e.occurredAt());
        } else if (event instanceof MoneyWithdrawn e) {
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("amount", e.amount().toString());
            d.put("balanceBefore", e.balanceBefore().toString());
            d.put("balanceAfter", e.balanceAfter().toString());
            saveRaw("WITHDRAWAL", e.accountNumber(), d, e.occurredAt());
        } else if (event instanceof AccountBlocked e) {
            saveRaw("ACCOUNT_BLOCKED", e.accountNumber(), Map.of("reason", e.reason()), e.occurredAt());
        } else if (event instanceof AccountOpened e) {
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("ownerId", e.ownerId());
            d.put("accountType", e.accountType().name());
            d.put("currency", e.currency());
            saveRaw("ACCOUNT_OPENED", e.accountNumber(), d, e.occurredAt());
        } else if (event instanceof LoanApproved e) {
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("approvedAmount", e.approvedAmount().toString());
            d.put("interestRate", e.interestRate());
            d.put("analystUserId", e.analystUserId());
            saveRaw("LOAN_APPROVED", String.valueOf(e.loanId()), d, e.occurredAt());
        } else if (event instanceof LoanRejected e) {
            saveRaw("LOAN_REJECTED", String.valueOf(e.loanId()),
                Map.of("reason", e.reason(), "analystUserId", e.analystUserId()), e.occurredAt());
        } else if (event instanceof LoanDisbursed e) {
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("disbursedAmount", e.disbursedAmount().toString());
            d.put("account", e.disbursementAccountNumber());
            saveRaw("LOAN_DISBURSED", String.valueOf(e.loanId()), d, e.occurredAt());
        } else if (event instanceof TransferExecuted e) {
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("amount", e.amount().toString());
            d.put("from", e.sourceAccount());
            d.put("to", e.destinationAccount());
            saveRaw("TRANSFER_EXECUTED", String.valueOf(e.transferId()), d, e.occurredAt());
        } else if (event instanceof TransferExpired e) {
            saveRaw("TRANSFER_EXPIRED", String.valueOf(e.transferId()),
                Map.of("minutesPending", e.minutesPending()), e.occurredAt());
        }
    }

    // ── Consultas ─────────────────────────────────────────────────────────────

    public List<AuditLog> findAll() {
        try {
            List<AuditLog> list = new ArrayList<>();
            ResultSet rs = conn.createStatement()
                .executeQuery("SELECT * FROM audit_logs ORDER BY operation_datetime DESC");
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException("Error listing audit logs", e); }
    }

    public List<AuditLog> findByProductId(String productId) {
        try {
            List<AuditLog> list = new ArrayList<>();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM audit_logs WHERE affected_product_id=? ORDER BY operation_datetime DESC");
            ps.setString(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException("Error finding audit logs", e); }
    }

    public List<AuditLog> findByUserId(int userId) {
        try {
            List<AuditLog> list = new ArrayList<>();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM audit_logs WHERE user_id=? ORDER BY operation_datetime DESC");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException("Error finding audit logs by user", e); }
    }

    // ── Persistencia interna ──────────────────────────────────────────────────

    private void saveRaw(String operationType, String affectedProductId,
                          Map<String, Object> detail, LocalDateTime occurredAt) {
        try {
            String sql = "INSERT INTO audit_logs (log_id, operation_type, operation_datetime, affected_product_id, detail_data) VALUES (?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, operationType);
            ps.setString(3, occurredAt.toString());
            ps.setString(4, affectedProductId);
            ps.setString(5, objectMapper.writeValueAsString(detail));
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("[AUDIT ERROR] " + e.getMessage());
        }
    }

    private AuditLog mapRow(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setLogId(rs.getString("log_id"));
        log.setOperationType(rs.getString("operation_type"));
        log.setOperationDateTime(LocalDateTime.parse(rs.getString("operation_datetime")));
        log.setAffectedProductId(rs.getString("affected_product_id"));
        String detail = rs.getString("detail_data");
        if (detail != null) {
            try {
                Map<String, Object> detailMap = objectMapper.readValue(detail, new TypeReference<>() {});
                log.setDetailData(detailMap);
            } catch (Exception e) {
                log.setDetailData(Map.of("raw", detail));
            }
        }
        return log;
    }
}
