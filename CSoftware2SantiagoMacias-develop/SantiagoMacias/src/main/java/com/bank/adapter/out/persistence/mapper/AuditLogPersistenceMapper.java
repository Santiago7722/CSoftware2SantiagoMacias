package com.bank.adapter.out.persistence.mapper;

import com.bank.adapter.out.persistence.entity.AuditLogJpaEntity;
import com.bank.domain.model.entity.AuditLog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * INFRASTRUCTURE MAPPER - AuditLogPersistenceMapper
 *
 * Serializes/deserializes the detailData Map to/from JSON string.
 * This simulates the NoSQL "document" behavior within a relational database.
 */
@Component
public class AuditLogPersistenceMapper {

    private final ObjectMapper objectMapper;

    public AuditLogPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AuditLog toDomain(AuditLogJpaEntity e) {
        if (e == null) return null;
        Map<String, Object> detail;
        try {
            detail = objectMapper.readValue(e.getDetailData(), new TypeReference<>() {});
        } catch (Exception ex) {
            detail = Map.of("raw", e.getDetailData());
        }
        AuditLog log = AuditLog.record(
            e.getOperationType(), e.getUserId(), e.getUserRole(),
            e.getAffectedProductId(), detail
        );
        log.setLogId(e.getLogId());
        return log;
    }

    public AuditLogJpaEntity toJpaEntity(AuditLog log) {
        if (log == null) return null;
        String detailJson;
        try {
            detailJson = objectMapper.writeValueAsString(log.getDetailData());
        } catch (Exception ex) {
            detailJson = "{}";
        }
        return AuditLogJpaEntity.builder()
            .logId(log.getLogId())
            .operationType(log.getOperationType())
            .operationDatetime(log.getOperationDateTime())
            .userId(log.getUserId())
            .userRole(log.getUserRole())
            .affectedProductId(log.getAffectedProductId())
            .detailData(detailJson)
            .build();
    }
}
