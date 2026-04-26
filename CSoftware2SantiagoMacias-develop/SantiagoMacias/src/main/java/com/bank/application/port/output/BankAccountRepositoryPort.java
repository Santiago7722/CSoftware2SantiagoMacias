package com.bank.application.port.output;

import com.bank.domain.model.aggregate.BankAccount;
import java.util.List;
import java.util.Optional;

/**
 * APPLICATION OUTPUT PORT - BankAccountRepositoryPort
 * Abstracts persistence for the BankAccount aggregate.
 */
public interface BankAccountRepositoryPort {
    BankAccount save(BankAccount account);
    Optional<BankAccount> findByAccountNumber(String accountNumber);
    List<BankAccount> findByOwnerId(String ownerId);
    List<BankAccount> findAll();
    boolean existsByAccountNumber(String accountNumber);
}
