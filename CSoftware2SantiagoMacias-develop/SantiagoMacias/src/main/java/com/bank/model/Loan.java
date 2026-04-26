package com.bank.model;

import com.bank.event.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AGGREGATE ROOT — Préstamo (DDD).
 *
 * Ciclo de vida: UNDER_REVIEW → APPROVED / REJECTED → DISBURSED
 * Todas las reglas de transición viven aquí, no en el servicio.
 */
public class Loan {

    private int loanId;
    private String loanType;
    private String clientId;
    private Money requestedAmount;
    private Money approvedAmount;
    private BigDecimal interestRate;
    private int termMonths;
    private LoanStatus status;
    private LocalDate approvalDate;
    private LocalDate disbursementDate;
    private String disbursementAccountNumber;
    private int creatorUserId;
    private int analystUserId;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public Loan() {}

    /** Factory method — crea una solicitud de préstamo nueva. */
    public static Loan request(String clientId, String loanType, Money requestedAmount,
                                int termMonths, String disbursementAccount, int creatorUserId) {
        if (clientId == null || clientId.isBlank()) throw new DomainException("Client ID is required.");
        if (loanType == null || loanType.isBlank()) throw new DomainException("Loan type is required.");
        if (requestedAmount == null || requestedAmount.isZero()) throw new DomainException("Requested amount must be greater than zero.");
        if (termMonths <= 0) throw new DomainException("Term must be greater than zero months.");

        Loan loan = new Loan();
        loan.clientId = clientId;
        loan.loanType = loanType;
        loan.requestedAmount = requestedAmount;
        loan.termMonths = termMonths;
        loan.disbursementAccountNumber = disbursementAccount;
        loan.creatorUserId = creatorUserId;
        loan.status = LoanStatus.UNDER_REVIEW;
        return loan;
    }

    // -------------------------------------------------------------------------
    // Comportamiento de dominio
    // -------------------------------------------------------------------------

    /** Aprueba el préstamo. Regla: debe estar UNDER_REVIEW. */
    public void approve(Money approvedAmount, BigDecimal interestRate, int termMonths, int analystUserId) {
        requireStatus(LoanStatus.UNDER_REVIEW, "approve");
        if (approvedAmount == null || approvedAmount.isZero())
            throw new DomainException("Approved amount must be greater than zero.");
        if (interestRate == null || interestRate.compareTo(BigDecimal.ZERO) <= 0)
            throw new DomainException("Interest rate must be greater than zero.");

        this.approvedAmount = approvedAmount;
        this.interestRate = interestRate;
        this.termMonths = termMonths > 0 ? termMonths : this.termMonths;
        this.analystUserId = analystUserId;
        this.status = LoanStatus.APPROVED;
        this.approvalDate = LocalDate.now();

        domainEvents.add(new LoanApproved(loanId, clientId, approvedAmount,
                interestRate.doubleValue(), this.termMonths, analystUserId));
    }

    /** Rechaza el préstamo. Regla: debe estar UNDER_REVIEW. */
    public void reject(String reason, int analystUserId) {
        requireStatus(LoanStatus.UNDER_REVIEW, "reject");
        this.analystUserId = analystUserId;
        this.status = LoanStatus.REJECTED;
        domainEvents.add(new LoanRejected(loanId, clientId,
                reason != null ? reason : "Not specified", analystUserId));
    }

    /** Marca el préstamo como desembolsado. Regla: debe estar APPROVED. */
    public void markAsDisbursed(int analystUserId) {
        requireStatus(LoanStatus.APPROVED, "disburse");
        if (disbursementAccountNumber == null || disbursementAccountNumber.isBlank())
            throw new DomainException("Disbursement account is not defined.");
        this.status = LoanStatus.DISBURSED;
        this.disbursementDate = LocalDate.now();
        domainEvents.add(new LoanDisbursed(loanId, clientId, approvedAmount,
                disbursementAccountNumber, analystUserId));
    }

    // -------------------------------------------------------------------------
    // Validaciones privadas
    // -------------------------------------------------------------------------

    private void requireStatus(LoanStatus required, String operation) {
        if (this.status != required)
            throw new DomainException("Cannot " + operation + " a loan in status " + status + ". Required: " + required);
    }

    // -------------------------------------------------------------------------
    // Domain Events
    // -------------------------------------------------------------------------

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return Collections.unmodifiableList(events);
    }

    // -------------------------------------------------------------------------
    // Getters y setters
    // -------------------------------------------------------------------------

    public int getLoanId() { return loanId; }
    public String getLoanType() { return loanType; }
    public String getClientId() { return clientId; }
    public Money getRequestedAmount() { return requestedAmount; }
    public Money getApprovedAmount() { return approvedAmount; }
    public BigDecimal getInterestRate() { return interestRate; }
    public int getTermMonths() { return termMonths; }
    public LoanStatus getStatus() { return status; }
    public LocalDate getApprovalDate() { return approvalDate; }
    public LocalDate getDisbursementDate() { return disbursementDate; }
    public String getDisbursementAccountNumber() { return disbursementAccountNumber; }
    public int getCreatorUserId() { return creatorUserId; }
    public int getAnalystUserId() { return analystUserId; }

    // Compatibilidad con ConsoleUI (usa BigDecimal directamente)
    public BigDecimal getRequestedAmountValue() { return requestedAmount != null ? requestedAmount.getAmount() : BigDecimal.ZERO; }
    public BigDecimal getApprovedAmountValue() { return approvedAmount != null ? approvedAmount.getAmount() : null; }

    public void setLoanId(int loanId) { this.loanId = loanId; }
    public void setLoanType(String loanType) { this.loanType = loanType; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public void setRequestedAmount(Money requestedAmount) { this.requestedAmount = requestedAmount; }
    public void setApprovedAmount(Money approvedAmount) { this.approvedAmount = approvedAmount; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    public void setTermMonths(int termMonths) { this.termMonths = termMonths; }
    public void setStatus(LoanStatus status) { this.status = status; }
    public void setApprovalDate(LocalDate approvalDate) { this.approvalDate = approvalDate; }
    public void setDisbursementDate(LocalDate disbursementDate) { this.disbursementDate = disbursementDate; }
    public void setDisbursementAccountNumber(String v) { this.disbursementAccountNumber = v; }
    public void setCreatorUserId(int creatorUserId) { this.creatorUserId = creatorUserId; }
    public void setAnalystUserId(int analystUserId) { this.analystUserId = analystUserId; }

    @Override
    public String toString() {
        return String.format("Loan[id=%d, type=%s, client=%s, requested=%s, status=%s]",
                loanId, loanType, clientId, requestedAmount, status);
    }
}
