package com.bank.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
public class AuditLogJpaEntity {

    @Id
    @Column(name = "log_id")
    private String logId;

    @Column(name = "operation_type", nullable = false)
    private String operationType;

    @Column(name = "operation_datetime", nullable = false)
    private LocalDateTime operationDatetime;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_role", nullable = false)
    private String userRole;

    @Column(name = "affected_product_id")
    private String affectedProductId;

    @Lob
    @Column(name = "detail_data", nullable = false, columnDefinition = "TEXT")
    private String detailData;

    public AuditLogJpaEntity() {}

    public String getLogId()                    { return logId; }
    public String getOperationType()            { return operationType; }
    public LocalDateTime getOperationDatetime() { return operationDatetime; }
    public Long getUserId()                     { return userId; }
    public String getUserRole()                 { return userRole; }
    public String getAffectedProductId()        { return affectedProductId; }
    public String getDetailData()               { return detailData; }

    public void setLogId(String v)              { this.logId = v; }
    public void setOperationType(String v)      { this.operationType = v; }
    public void setOperationDatetime(LocalDateTime v) { this.operationDatetime = v; }
    public void setUserId(Long v)               { this.userId = v; }
    public void setUserRole(String v)           { this.userRole = v; }
    public void setAffectedProductId(String v)  { this.affectedProductId = v; }
    public void setDetailData(String v)         { this.detailData = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String logId, operationType, userRole, affectedProductId, detailData;
        private LocalDateTime operationDatetime;
        private Long userId;
        public Builder logId(String v)               { this.logId = v; return this; }
        public Builder operationType(String v)       { this.operationType = v; return this; }
        public Builder operationDatetime(LocalDateTime v) { this.operationDatetime = v; return this; }
        public Builder userId(Long v)                { this.userId = v; return this; }
        public Builder userRole(String v)            { this.userRole = v; return this; }
        public Builder affectedProductId(String v)   { this.affectedProductId = v; return this; }
        public Builder detailData(String v)          { this.detailData = v; return this; }
        public AuditLogJpaEntity build() {
            AuditLogJpaEntity e = new AuditLogJpaEntity();
            e.logId = logId; e.operationType = operationType;
            e.operationDatetime = operationDatetime; e.userId = userId;
            e.userRole = userRole; e.affectedProductId = affectedProductId;
            e.detailData = detailData;
            return e;
        }
    }
}
