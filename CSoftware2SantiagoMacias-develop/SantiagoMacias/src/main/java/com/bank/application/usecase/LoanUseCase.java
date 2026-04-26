package com.bank.application.usecase;

import com.bank.application.dto.BankingDto.*;
import com.bank.application.port.input.LoanInputPort;
import com.bank.application.port.output.AuditLogRepositoryPort;
import com.bank.application.port.output.BankAccountRepositoryPort;
import com.bank.application.port.output.LoanRepositoryPort;
import com.bank.application.port.output.UserRepositoryPort;
import com.bank.domain.exception.AccessDeniedException;
import com.bank.domain.exception.DomainValidationException;
import com.bank.domain.exception.ResourceNotFoundException;
import com.bank.domain.model.aggregate.BankAccount;
import com.bank.domain.model.aggregate.Loan;
import com.bank.domain.model.entity.AuditLog;
import com.bank.domain.model.entity.User;
import com.bank.domain.model.valueobject.*;
import com.bank.domain.service.LoanDisbursementDomainService;
import com.bank.shared.SecurityContextHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * APPLICATION USE CASE - LoanUseCase
 *
 * Orchestrates the full loan lifecycle:
 * REQUEST → UNDER_REVIEW → APPROVED/REJECTED → DISBURSED
 *
 * Uses LoanDisbursementDomainService for cross-aggregate validation.
 */
@Service
@Transactional
public class LoanUseCase implements LoanInputPort {

    private final LoanRepositoryPort loanRepository;
    private final BankAccountRepositoryPort accountRepository;
    private final UserRepositoryPort userRepository;
    private final AuditLogRepositoryPort auditLogRepository;
    private final LoanDisbursementDomainService disbursementService;

    public LoanUseCase(LoanRepositoryPort loanRepository,
                       BankAccountRepositoryPort accountRepository,
                       UserRepositoryPort userRepository,
                       AuditLogRepositoryPort auditLogRepository,
                       LoanDisbursementDomainService disbursementService) {
        this.loanRepository = loanRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
        this.disbursementService = disbursementService;
    }

    @Override
    public LoanResponse requestLoan(RequestLoanCommand command) {
        SecurityContextHelper.requireAnyRole(
            UserRole.CLIENT_INDIVIDUAL, UserRole.CLIENT_COMPANY,
            UserRole.COMMERCIAL_EMPLOYEE, UserRole.INTERNAL_ANALYST
        );
        User current = SecurityContextHelper.getCurrentUser();

        // Determine client: clients request for themselves, employees for a specific client
        String clientId;
        if (current.isClientRole()) {
            clientId = current.getIdentificationNumber();
        } else {
            if (command.clientIdentificationNumber() == null || command.clientIdentificationNumber().isBlank())
                throw new DomainValidationException("Client identification number is required when requesting on behalf of a client.");
            clientId = command.clientIdentificationNumber();
        }

        // Validate client is active
        User client = userRepository.findByIdentificationNumber(clientId)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + clientId));
        if (!client.isActive())
            throw new DomainValidationException("Client is not ACTIVE. Cannot request a loan.");

        Money requestedAmount = new Money(command.requestedAmount(), command.currency());
        Loan loan = Loan.request(clientId, command.loanType(), requestedAmount,
            command.termMonths(), command.disbursementAccountNumber(), current.getId());

        Loan saved = loanRepository.save(loan);
        audit("LOAN_REQUESTED", String.valueOf(saved.getId()), Map.of(
            "clientId", clientId,
            "loanType", command.loanType(),
            "requestedAmount", command.requestedAmount(),
            "currency", command.currency(),
            "termMonths", command.termMonths(),
            "status", "UNDER_REVIEW"
        ));
        return toResponse(saved);
    }

    @Override
    public LoanResponse approveLoan(Long loanId, ApproveLoanCommand command) {
        SecurityContextHelper.requireAnyRole(UserRole.INTERNAL_ANALYST);
        User analyst = SecurityContextHelper.getCurrentUser();
        Loan loan = findLoanOrThrow(loanId);
        String previousStatus = loan.getStatus().name();

        Money approvedAmount = new Money(command.approvedAmount(), command.currency());
        loan.approve(approvedAmount, command.interestRate(), command.termMonths(), analyst.getId());

        Loan saved = loanRepository.save(loan);
        audit("LOAN_APPROVED", String.valueOf(loanId), buildStateChangeDetail(
            previousStatus, "APPROVED",
            Map.of("approvedAmount", command.approvedAmount(),
                   "interestRate", command.interestRate(),
                   "termMonths", command.termMonths(),
                   "analystId", analyst.getId())
        ));
        return toResponse(saved);
    }

    @Override
    public LoanResponse rejectLoan(Long loanId, RejectLoanCommand command) {
        SecurityContextHelper.requireAnyRole(UserRole.INTERNAL_ANALYST);
        User analyst = SecurityContextHelper.getCurrentUser();
        Loan loan = findLoanOrThrow(loanId);
        String previousStatus = loan.getStatus().name();

        loan.reject(analyst.getId());

        Loan saved = loanRepository.save(loan);
        audit("LOAN_REJECTED", String.valueOf(loanId), buildStateChangeDetail(
            previousStatus, "REJECTED",
            Map.of("reason", command.reason(), "analystId", analyst.getId())
        ));
        return toResponse(saved);
    }

    @Override
    public LoanResponse disburseLoan(Long loanId) {
        SecurityContextHelper.requireAnyRole(UserRole.INTERNAL_ANALYST);
        User analyst = SecurityContextHelper.getCurrentUser();
        Loan loan = findLoanOrThrow(loanId);

        BankAccount disbursementAccount = accountRepository
            .findByAccountNumber(loan.getDisbursementAccountNumber())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Disbursement account not found: " + loan.getDisbursementAccountNumber()));

        // Domain service handles cross-aggregate validation + state changes
        disbursementService.disburse(loan, disbursementAccount);

        loanRepository.save(loan);
        accountRepository.save(disbursementAccount);

        audit("LOAN_DISBURSED", String.valueOf(loanId), Map.of(
            "disbursedAmount", loan.getApprovedAmount().getAmount(),
            "currency", loan.getApprovedAmount().getCurrency(),
            "disbursementAccount", disbursementAccount.getAccountNumber(),
            "newAccountBalance", disbursementAccount.getBalance().getAmount(),
            "analystId", analyst.getId()
        ));
        return toResponse(loan);
    }

    @Override
    @Transactional(readOnly = true)
    public LoanResponse getLoanById(Long loanId) {
        SecurityContextHelper.requireLogin();
        Loan loan = findLoanOrThrow(loanId);
        assertCanViewLoan(loan);
        return toResponse(loan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getLoansByClient(String clientIdentificationNumber) {
        SecurityContextHelper.requireLogin();
        User current = SecurityContextHelper.getCurrentUser();
        if (current.isClientRole() && !current.getIdentificationNumber().equals(clientIdentificationNumber))
            throw new AccessDeniedException("You can only view your own loans.");
        return loanRepository.findByClientId(clientIdentificationNumber)
            .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getAllLoans() {
        SecurityContextHelper.requireAnyRole(UserRole.INTERNAL_ANALYST, UserRole.COMMERCIAL_EMPLOYEE);
        return loanRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getLoansByStatus(String status) {
        SecurityContextHelper.requireAnyRole(UserRole.INTERNAL_ANALYST, UserRole.COMMERCIAL_EMPLOYEE);
        LoanStatus loanStatus = LoanStatus.valueOf(status.toUpperCase());
        return loanRepository.findByStatus(loanStatus).stream().map(this::toResponse).toList();
    }

    // ─── Helpers ────────────────────────────────────────────────────────

    private Loan findLoanOrThrow(Long id) {
        return loanRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Loan not found: " + id));
    }

    private void assertCanViewLoan(Loan loan) {
        User current = SecurityContextHelper.getCurrentUser();
        if (current.isClientRole() && !current.getIdentificationNumber().equals(loan.getClientId()))
            throw new AccessDeniedException("You can only view your own loans.");
    }

    private Map<String, Object> buildStateChangeDetail(String prev, String next, Map<String, Object> extra) {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("previousStatus", prev);
        detail.put("newStatus", next);
        detail.putAll(extra);
        return detail;
    }

    private void audit(String op, String productId, Map<String, Object> detail) {
        User current = SecurityContextHelper.getCurrentUser();
        if (current == null) return;
        auditLogRepository.save(AuditLog.record(op, current.getId(), current.getRole().name(), productId, detail));
    }

    LoanResponse toResponse(Loan l) {
        String currency = l.getRequestedAmount() != null ? l.getRequestedAmount().getCurrency() : "USD";
        return new LoanResponse(
            l.getId(), l.getLoanType(), l.getClientId(),
            l.getRequestedAmount() != null ? l.getRequestedAmount().getAmount() : null,
            l.getApprovedAmount() != null ? l.getApprovedAmount().getAmount() : null,
            l.getInterestRate(), l.getTermMonths(), l.getStatus(),
            l.getApprovalDate(), l.getDisbursementDate(), l.getDisbursementAccountNumber(), currency
        );
    }
}
