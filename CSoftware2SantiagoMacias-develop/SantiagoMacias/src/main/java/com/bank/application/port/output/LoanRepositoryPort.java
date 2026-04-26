package com.bank.application.port.output;

import com.bank.domain.model.aggregate.Loan;
import com.bank.domain.model.valueobject.LoanStatus;
import java.util.List;
import java.util.Optional;

/**
 * APPLICATION OUTPUT PORT - LoanRepositoryPort
 * Abstracts persistence for the Loan aggregate.
 */
public interface LoanRepositoryPort {
    Loan save(Loan loan);
    Optional<Loan> findById(Long id);
    List<Loan> findByClientId(String clientId);
    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findAll();
}
