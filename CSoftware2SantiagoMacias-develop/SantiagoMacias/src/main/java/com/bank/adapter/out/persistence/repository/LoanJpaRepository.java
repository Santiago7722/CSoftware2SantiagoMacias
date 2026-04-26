package com.bank.adapter.out.persistence.repository;

import com.bank.adapter.out.persistence.entity.LoanJpaEntity;
import com.bank.domain.model.valueobject.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanJpaRepository extends JpaRepository<LoanJpaEntity, Long> {
    List<LoanJpaEntity> findByClientId(String clientId);
    List<LoanJpaEntity> findByStatus(LoanStatus status);
}
