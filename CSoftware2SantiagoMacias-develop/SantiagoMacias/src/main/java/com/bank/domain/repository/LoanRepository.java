package com.bank.domain.repository;

import com.bank.domain.model.aggregate.Loan;
import com.bank.domain.model.valueobject.LoanStatus;

import java.util.List;
import java.util.Optional;

/**
 * DOMAIN REPOSITORY INTERFACE (Output Port at Domain Level)
 * Defines persistence operations for Loan aggregate.
 */
public interface LoanRepository {
    Loan save(Loan loan);
    Optional<Loan> findById(Long id);
    List<Loan> findByClientId(String clientId);
    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findAll();
}
