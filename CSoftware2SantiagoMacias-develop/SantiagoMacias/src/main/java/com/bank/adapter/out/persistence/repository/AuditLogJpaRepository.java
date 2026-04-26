package com.bank.adapter.out.persistence.repository;

import com.bank.adapter.out.persistence.entity.AuditLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogJpaRepository extends JpaRepository<AuditLogJpaEntity, String> {
    List<AuditLogJpaEntity> findByAffectedProductId(String affectedProductId);
    List<AuditLogJpaEntity> findByUserId(Long userId);
}
