package com.bank.repository;
import com.bank.model.Loan;
import com.bank.model.LoanStatus;
import java.util.List;
import java.util.Optional;
public interface LoanRepository {
    void save(Loan loan);
    Optional<Loan> findById(int loanId);
    List<Loan> findByClientId(String clientId);
    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findAll();
}
