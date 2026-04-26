package com.bank.application.usecase;

import com.bank.application.dto.BankingDto.*;
import com.bank.application.port.input.TransferInputPort;
import com.bank.application.port.output.*;
import com.bank.domain.exception.AccessDeniedException;
import com.bank.domain.exception.ResourceNotFoundException;
import com.bank.domain.model.aggregate.BankAccount;
import com.bank.domain.model.aggregate.Transfer;
import com.bank.domain.model.entity.AuditLog;
import com.bank.domain.model.entity.User;
import com.bank.domain.model.valueobject.*;
import com.bank.domain.service.TransferDomainService;
import com.bank.shared.SecurityContextHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * APPLICATION USE CASE - TransferUseCase
 *
 * Orchestrates transfer creation, approval, rejection, and auto-expiry.
 * Delegates business rules to the Transfer aggregate and TransferDomainService.
 */
@Service
@Transactional
public class TransferUseCase implements TransferInputPort {

    private final TransferRepositoryPort transferRepository;
    private final BankAccountRepositoryPort accountRepository;
    private final AuditLogRepositoryPort auditLogRepository;
    private final TransferDomainService transferDomainService;

    public TransferUseCase(TransferRepositoryPort transferRepository,
                           BankAccountRepositoryPort accountRepository,
                           AuditLogRepositoryPort auditLogRepository,
                           TransferDomainService transferDomainService) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
        this.auditLogRepository = auditLogRepository;
        this.transferDomainService = transferDomainService;
    }

    @Override
    public TransferResponse createTransfer(CreateTransferCommand command) {
        SecurityContextHelper.requireAnyRole(
            UserRole.CLIENT_INDIVIDUAL, UserRole.CLIENT_COMPANY,
            UserRole.COMPANY_EMPLOYEE, UserRole.INTERNAL_ANALYST
        );
        User current = SecurityContextHelper.getCurrentUser();

        BankAccount source = findAccountOrThrow(command.sourceAccount());
        BankAccount destination = findAccountOrThrow(command.destinationAccount());

        // Access control: clients can only use their own accounts
        if (current.getRole() == UserRole.CLIENT_INDIVIDUAL
                && !source.belongsTo(current.getIdentificationNumber())) {
            throw new AccessDeniedException("You can only transfer from your own accounts.");
        }
        if (current.getRole() == UserRole.COMPANY_EMPLOYEE
                && !source.belongsTo(current.getCompanyId())) {
            throw new AccessDeniedException("You can only transfer from your company's accounts.");
        }

        String currency = source.getBalance().getCurrency();
        Money amount = new Money(command.amount(), currency);

        boolean isCompanyEmployee = current.getRole() == UserRole.COMPANY_EMPLOYEE;
        boolean needsApproval = transferDomainService.requiresApproval(amount, isCompanyEmployee);

        Transfer transfer;
        if (needsApproval) {
            transfer = Transfer.createPendingApproval(
                command.sourceAccount(), command.destinationAccount(), amount, current.getId());
            Transfer saved = transferRepository.save(transfer);
            audit("TRANSFER_PENDING_APPROVAL", String.valueOf(saved.getId()), Map.of(
                "amount", command.amount(), "currency", currency,
                "sourceAccount", command.sourceAccount(),
                "destinationAccount", command.destinationAccount(),
                "approvalThreshold", transferDomainService.getApprovalThreshold()
            ));
            return toResponse(saved);
        } else {
            transfer = Transfer.createDirect(
                command.sourceAccount(), command.destinationAccount(), amount, current.getId());
            // Execute immediately: debit source, credit destination
            transferDomainService.executeTransfer(transfer, source, destination);
            accountRepository.save(source);
            accountRepository.save(destination);
            Transfer saved = transferRepository.save(transfer);
            auditExecuted(saved, source, destination);
            return toResponse(saved);
        }
    }

    @Override
    public TransferResponse approveTransfer(Long transferId) {
        SecurityContextHelper.requireAnyRole(
            UserRole.COMPANY_SUPERVISOR, UserRole.CLIENT_COMPANY, UserRole.INTERNAL_ANALYST);
        User approver = SecurityContextHelper.getCurrentUser();

        Transfer transfer = findTransferOrThrow(transferId);

        // Auto-expire check before approving
        if (transfer.isExpired()) {
            transfer.expire();
            transferRepository.save(transfer);
            auditExpired(transfer);
            throw new AccessDeniedException("Transfer " + transferId + " has expired and cannot be approved.");
        }

        BankAccount source = findAccountOrThrow(transfer.getSourceAccount());
        BankAccount destination = findAccountOrThrow(transfer.getDestinationAccount());

        // Validate supervisor belongs to the company
        if (approver.getRole() == UserRole.COMPANY_SUPERVISOR
                && !source.belongsTo(approver.getCompanyId())) {
            throw new AccessDeniedException("You can only approve transfers from your company's accounts.");
        }

        // Domain service validates funds and executes
        transferDomainService.executeTransfer(transfer, source, destination);
        transfer.approve(approver.getId());

        accountRepository.save(source);
        accountRepository.save(destination);
        Transfer saved = transferRepository.save(transfer);
        auditExecuted(saved, source, destination);
        return toResponse(saved);
    }

    @Override
    public TransferResponse rejectTransfer(Long transferId, ApproveRejectTransferCommand command) {
        SecurityContextHelper.requireAnyRole(
            UserRole.COMPANY_SUPERVISOR, UserRole.CLIENT_COMPANY, UserRole.INTERNAL_ANALYST);
        User approver = SecurityContextHelper.getCurrentUser();

        Transfer transfer = findTransferOrThrow(transferId);
        transfer.reject(approver.getId());
        Transfer saved = transferRepository.save(transfer);

        audit("TRANSFER_REJECTED", String.valueOf(transferId), Map.of(
            "reason", command.reason(), "approverId", approver.getId()));
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TransferResponse getTransferById(Long transferId) {
        SecurityContextHelper.requireLogin();
        Transfer transfer = findTransferOrThrow(transferId);
        return toResponse(transfer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransferResponse> getTransfersByAccount(String accountNumber) {
        SecurityContextHelper.requireLogin();
        User current = SecurityContextHelper.getCurrentUser();
        if (current.getRole() == UserRole.CLIENT_INDIVIDUAL) {
            BankAccount acc = findAccountOrThrow(accountNumber);
            if (!acc.belongsTo(current.getIdentificationNumber()))
                throw new AccessDeniedException("You can only view transfers for your own accounts.");
        }
        return transferRepository.findBySourceAccountOrDestinationAccount(accountNumber)
            .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransferResponse> getPendingTransfers() {
        SecurityContextHelper.requireAnyRole(
            UserRole.COMPANY_SUPERVISOR, UserRole.CLIENT_COMPANY, UserRole.INTERNAL_ANALYST);
        processExpiredTransfers();
        return transferRepository.findByStatus(TransferStatus.PENDING_APPROVAL)
            .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransferResponse> getAllTransfers() {
        SecurityContextHelper.requireAnyRole(UserRole.INTERNAL_ANALYST);
        return transferRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public int processExpiredTransfers() {
        List<Transfer> pending = transferRepository.findByStatus(TransferStatus.PENDING_APPROVAL);
        int count = 0;
        for (Transfer t : pending) {
            if (t.isExpired()) {
                t.expire();
                transferRepository.save(t);
                auditExpired(t);
                count++;
            }
        }
        return count;
    }

    // ─── Private Helpers ────────────────────────────────────────────────

    private BankAccount findAccountOrThrow(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
    }

    private Transfer findTransferOrThrow(Long id) {
        return transferRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transfer not found: " + id));
    }

    private void auditExecuted(Transfer t, BankAccount source, BankAccount destination) {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("amount", t.getAmount().getAmount());
        detail.put("currency", t.getAmount().getCurrency());
        detail.put("sourceAccount", source.getAccountNumber());
        detail.put("sourceBalanceAfter", source.getBalance().getAmount());
        detail.put("destinationAccount", destination.getAccountNumber());
        detail.put("destinationBalanceAfter", destination.getBalance().getAmount());
        audit("TRANSFER_EXECUTED", String.valueOf(t.getId()), detail);
    }

    private void auditExpired(Transfer t) {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("reason", "Expired due to lack of approval within the established time");
        detail.put("minutesPending", t.getMinutesPending());
        detail.put("creatorUserId", t.getCreatorUserId());
        auditLogRepository.save(AuditLog.record(
            "TRANSFER_EXPIRED", t.getCreatorUserId(), "SYSTEM",
            String.valueOf(t.getId()), detail));
    }

    private void audit(String op, String productId, Map<String, Object> detail) {
        User current = SecurityContextHelper.getCurrentUser();
        if (current == null) return;
        auditLogRepository.save(AuditLog.record(
            op, current.getId(), current.getRole().name(), productId, detail));
    }

    TransferResponse toResponse(Transfer t) {
        return new TransferResponse(
            t.getId(), t.getSourceAccount(), t.getDestinationAccount(),
            t.getAmount().getAmount(), t.getAmount().getCurrency(),
            t.getCreationDateTime(), t.getApprovalDateTime(),
            t.getStatus(), t.getCreatorUserId(), t.getApproverUserId()
        );
    }
}
