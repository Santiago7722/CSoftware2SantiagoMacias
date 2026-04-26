package com.bank.domain.repository;

import com.bank.domain.model.aggregate.BankAccount;

import java.util.List;
import java.util.Optional;

/**
 * DOMAIN REPOSITORY INTERFACE (Output Port at Domain Level)
 * Defines persistence operations for BankAccount aggregate.
 */
public interface BankAccountRepository {
    BankAccount save(BankAccount account);
    Optional<BankAccount> findByAccountNumber(String accountNumber);
    List<BankAccount> findByOwnerId(String ownerId);
    List<BankAccount> findAll();
    boolean existsByAccountNumber(String accountNumber);
}
