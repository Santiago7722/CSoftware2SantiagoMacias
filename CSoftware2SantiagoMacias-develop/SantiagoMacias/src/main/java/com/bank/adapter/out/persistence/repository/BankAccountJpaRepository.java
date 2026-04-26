package com.bank.adapter.out.persistence.repository;

import com.bank.adapter.out.persistence.entity.BankAccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountJpaRepository extends JpaRepository<BankAccountJpaEntity, Long> {
    Optional<BankAccountJpaEntity> findByAccountNumber(String accountNumber);
    List<BankAccountJpaEntity> findByOwnerId(String ownerId);
    boolean existsByAccountNumber(String accountNumber);
}
