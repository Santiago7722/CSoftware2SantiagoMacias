package com.bank.service;

import com.bank.model.*;
import com.bank.repository.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * APPLICATION SERVICE — Casos de uso de préstamos (DDD).
 */
public class LoanService {

    private final LoanRepository loanRepo;
    private final AccountRepository accountRepo;
    private final UserRepository userRepo;
    private final DomainEventPublisher eventPublisher;

    public LoanService() {
        this.loanRepo = new SqliteLoanRepository();
        this.accountRepo = new SqliteAccountRepository();
        this.userRepo = new SqliteUserRepository();
        this.eventPublisher = new AuditLogRepository();
    }

    public Loan requestLoan(String clientId, String loanType, BigDecimal requestedAmount,
                             int termMonths, String disbursementAccount) {
        AuthService.requireRole(
            UserRole.CLIENT_INDIVIDUAL, UserRole.CLIENT_COMPANY,
            UserRole.COMMERCIAL_EMPLOYEE, UserRole.INTERNAL_ANALYST
        );
        User current = AuthService.getCurrentUser();

        if (current.hasRole(UserRole.CLIENT_INDIVIDUAL, UserRole.CLIENT_COMPANY)) {
            if (!current.isOwner(clientId))
                throw new DomainException("Clients can only request loans for themselves.");
        }

        User client = userRepo.findByIdentification(clientId)
            .orElseThrow(() -> new DomainException("Client not found: " + clientId));
        if (!client.isOperational())
            throw new DomainException("Client is not ACTIVE. Cannot request a loan.");

        Loan loan = Loan.request(clientId, loanType,
                new Money(requestedAmount, "USD"), termMonths, disbursementAccount, current.getUserId());

        loanRepo.save(loan);
        eventPublisher.publishAll(loan.pullDomainEvents());
        System.out.println("[LOAN] Request submitted. ID: " + loan.getLoanId() + " | Status: UNDER_REVIEW");
        return loan;
    }

    public Loan approveLoan(int loanId, BigDecimal approvedAmount, BigDecimal interestRate, int termMonths) {
        AuthService.requireRole(UserRole.INTERNAL_ANALYST);
        User analyst = AuthService.getCurrentUser();

        Loan loan = loadLoan(loanId);
        loan.approve(new Money(approvedAmount, "USD"), interestRate, termMonths, analyst.getUserId());

        loanRepo.save(loan);
        eventPublisher.publishAll(loan.pullDomainEvents());
        System.out.println("[LOAN] Loan " + loanId + " APPROVED. Amount: " + approvedAmount);
        return loan;
    }

    public Loan rejectLoan(int loanId, String reason) {
        AuthService.requireRole(UserRole.INTERNAL_ANALYST);
        User analyst = AuthService.getCurrentUser();

        Loan loan = loadLoan(loanId);
        loan.reject(reason, analyst.getUserId());

        loanRepo.save(loan);
        eventPublisher.publishAll(loan.pullDomainEvents());
        System.out.println("[LOAN] Loan " + loanId + " REJECTED.");
        return loan;
    }

    public Loan disburseLoan(int loanId) {
        AuthService.requireRole(UserRole.INTERNAL_ANALYST);
        User analyst = AuthService.getCurrentUser();

        Loan loan = loadLoan(loanId);

        BankAccount disbAccount = accountRepo.findByAccountNumber(loan.getDisbursementAccountNumber())
            .orElseThrow(() -> new DomainException("Disbursement account not found: " + loan.getDisbursementAccountNumber()));

        if (disbAccount.getStatus() != AccountStatus.ACTIVE)
            throw new DomainException("Disbursement account is not ACTIVE.");
        if (!disbAccount.getOwnerId().equals(loan.getClientId()))
            throw new DomainException("Disbursement account does not belong to the loan client.");

        loan.markAsDisbursed(analyst.getUserId());
        disbAccount.deposit(loan.getApprovedAmount());

        loanRepo.save(loan);
        accountRepo.save(disbAccount);
        eventPublisher.publishAll(loan.pullDomainEvents());
        eventPublisher.publishAll(disbAccount.pullDomainEvents());

        System.out.printf("[LOAN] Loan %d DISBURSED. Amount: %s credited to %s%n",
                loanId, loan.getApprovedAmount(), disbAccount.getAccountNumber());
        return loan;
    }

    public List<Loan> getLoansByClient(String clientId) {
        AuthService.requireLogin();
        User current = AuthService.getCurrentUser();
        if (current.hasRole(UserRole.CLIENT_INDIVIDUAL, UserRole.CLIENT_COMPANY)) {
            if (!current.isOwner(clientId))
                throw new DomainException("You can only view your own loans.");
        }
        return loanRepo.findByClientId(clientId);
    }

    public List<Loan> getAllLoans() {
        AuthService.requireRole(UserRole.INTERNAL_ANALYST, UserRole.COMMERCIAL_EMPLOYEE);
        return loanRepo.findAll();
    }

    public List<Loan> getLoansByStatus(LoanStatus status) {
        AuthService.requireRole(UserRole.INTERNAL_ANALYST, UserRole.COMMERCIAL_EMPLOYEE);
        return loanRepo.findByStatus(status);
    }

    private Loan loadLoan(int loanId) {
        return loanRepo.findById(loanId)
            .orElseThrow(() -> new DomainException("Loan not found: " + loanId));
    }
}
