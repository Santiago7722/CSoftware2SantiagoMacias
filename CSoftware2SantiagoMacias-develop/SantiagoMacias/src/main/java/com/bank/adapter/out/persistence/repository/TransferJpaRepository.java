package com.bank.adapter.out.persistence.repository;

import com.bank.adapter.out.persistence.entity.TransferJpaEntity;
import com.bank.domain.model.valueobject.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferJpaRepository extends JpaRepository<TransferJpaEntity, Long> {
    List<TransferJpaEntity> findByStatus(TransferStatus status);

    @Query("SELECT t FROM TransferJpaEntity t WHERE t.sourceAccount = :acc OR t.destinationAccount = :acc")
    List<TransferJpaEntity> findBySourceOrDestination(@Param("acc") String accountNumber);
}
