package com.bank.model;

import java.time.LocalDateTime;
import java.util.Map;

public class AuditLog {
    private String logId;
    private String operationType;
    private LocalDateTime operationDateTime;
    private int userId;
    private String userRole;
    private String affectedProductId;
    private Map<String, Object> detailData;

    public AuditLog() {}

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }

    public LocalDateTime getOperationDateTime() { return operationDateTime; }
    public void setOperationDateTime(LocalDateTime operationDateTime) { this.operationDateTime = operationDateTime; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getAffectedProductId() { return affectedProductId; }
    public void setAffectedProductId(String affectedProductId) { this.affectedProductId = affectedProductId; }

    public Map<String, Object> getDetailData() { return detailData; }
    public void setDetailData(Map<String, Object> detailData) { this.detailData = detailData; }

    @Override
    public String toString() {
        return String.format("AuditLog[id=%s, op=%s, user=%d, product=%s, time=%s]",
                logId, operationType, userId, affectedProductId, operationDateTime);
    }
}
