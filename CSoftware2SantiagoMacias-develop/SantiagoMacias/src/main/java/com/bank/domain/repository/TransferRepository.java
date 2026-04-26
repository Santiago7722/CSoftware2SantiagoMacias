package com.bank.domain.repository;

import com.bank.domain.model.aggregate.Transfer;
import com.bank.domain.model.valueobject.TransferStatus;

import java.util.List;
import java.util.Optional;

/**
 * DOMAIN REPOSITORY INTERFACE (Output Port at Domain Level)
 * Defines persistence operations for Transfer aggregate.
 */
public interface TransferRepository {
    Transfer save(Transfer transfer);
    Optional<Transfer> findById(Long id);
    List<Transfer> findByStatus(TransferStatus status);
    List<Transfer> findBySourceAccountOrDestinationAccount(String accountNumber);
    List<Transfer> findAll();
}
