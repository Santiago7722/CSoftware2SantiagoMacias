package com.bank.service;

import com.bank.model.*;
import com.bank.repository.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * APPLICATION SERVICE — Casos de uso de transferencias (DDD).
 */
public class TransferService {

    private static final Money APPROVAL_THRESHOLD = Money.of(5000.00, "USD");

    private final TransferRepository transferRepo;
    private final AccountRepository accountRepo;
    private final DomainEventPublisher eventPublisher;

    public TransferService() {
        this.transferRepo = new SqliteTransferRepository();
        this.accountRepo = new SqliteAccountRepository();
        this.eventPublisher = new AuditLogRepository();
    }

    public Transfer createTransfer(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount) {
        AuthService.requireRole(
            UserRole.CLIENT_INDIVIDUAL, UserRole.CLIENT_COMPANY,
            UserRole.COMPANY_EMPLOYEE, UserRole.INTERNAL_ANALYST
        );
        User current = AuthService.getCurrentUser();

        BankAccount source = loadAccount(sourceAccountNumber);
        BankAccount destination = loadAccount(destinationAccountNumber);

        if (current.hasRole(UserRole.CLIENT_INDIVIDUAL)) {
            if (!source.getOwnerId().equals(current.getIdentificationNumber()))
                throw new DomainException("You can only transfer from your own accounts.");
        }
        if (current.hasRole(UserRole.COMPANY_EMPLOYEE)) {
            if (!source.getOwnerId().equals(current.getCompanyId()))
                throw new DomainException("You can only transfer from your company's accounts.");
        }

        Money transferAmount = new Money(amount, source.getBalance().getCurrency());
        Transfer transfer = Transfer.create(sourceAccountNumber, destinationAccountNumber,
                transferAmount, current.getUserId());

        boolean needsApproval = current.hasRole(UserRole.COMPANY_EMPLOYEE)
                && transferAmount.isGreaterThan(APPROVAL_THRESHOLD);

        if (needsApproval) {
            transferRepo.save(transfer);
            eventPublisher.publishAll(transfer.pullDomainEvents());
            System.out.printf("[TRANSFER] Created PENDING APPROVAL. ID: %d | Amount: %s%n",
                    transfer.getTransferId(), transferAmount);
        } else {
            // Mover fondos a través del aggregate
            source.withdraw(transferAmount);
            destination.deposit(transferAmount);
            transfer.execute(current.getUserId());

            transferRepo.save(transfer);
            accountRepo.save(source);
            accountRepo.save(destination);
            eventPublisher.publishAll(transfer.pullDomainEvents());
            eventPublisher.publishAll(source.pullDomainEvents());
            eventPublisher.publishAll(destination.pullDomainEvents());
            System.out.printf("[TRANSFER] EXECUTED. ID: %d | Amount: %s | %s → %s%n",
                    transfer.getTransferId(), transferAmount, sourceAccountNumber, destinationAccountNumber);
        }

        return transfer;
    }

    public Transfer approveTransfer(int transferId) {
        AuthService.requireRole(UserRole.COMPANY_SUPERVISOR, UserRole.CLIENT_COMPANY);
        User current = AuthService.getCurrentUser();

        Transfer transfer = loadTransfer(transferId);
        BankAccount source = loadAccount(transfer.getSourceAccount());
        BankAccount destination = loadAccount(transfer.getDestinationAccount());

        if (current.hasRole(UserRole.COMPANY_SUPERVISOR)) {
            if (!source.getOwnerId().equals(current.getCompanyId()))
                throw new DomainException("You can only approve transfers from your company's accounts.");
        }

        source.withdraw(transfer.getAmount());
        destination.deposit(transfer.getAmount());
        transfer.approve(current.getUserId());

        transferRepo.save(transfer);
        accountRepo.save(source);
        accountRepo.save(destination);
        eventPublisher.publishAll(transfer.pullDomainEvents());
        eventPublisher.publishAll(source.pullDomainEvents());
        eventPublisher.publishAll(destination.pullDomainEvents());

        System.out.println("[TRANSFER] Transfer " + transferId + " APPROVED and EXECUTED.");
        return transfer;
    }

    public Transfer rejectTransfer(int transferId, String reason) {
        AuthService.requireRole(UserRole.COMPANY_SUPERVISOR, UserRole.CLIENT_COMPANY);
        User current = AuthService.getCurrentUser();

        Transfer transfer = loadTransfer(transferId);
        transfer.reject(reason, current.getUserId());

        transferRepo.save(transfer);
        System.out.println("[TRANSFER] Transfer " + transferId + " REJECTED.");
        return transfer;
    }

    public void processExpiredTransfers() {
        List<Transfer> pending = transferRepo.findByStatus(TransferStatus.PENDING_APPROVAL);
        for (Transfer t : pending) {
            boolean expired = t.expireIfOverdue();
            if (expired) {
                transferRepo.save(t);
                eventPublisher.publishAll(t.pullDomainEvents());
            }
        }
    }

    public List<Transfer> getTransfersForAccount(String accountNumber) {
        AuthService.requireLogin();
        User current = AuthService.getCurrentUser();
        if (current.hasRole(UserRole.CLIENT_INDIVIDUAL)) {
            BankAccount acc = loadAccount(accountNumber);
            if (!acc.getOwnerId().equals(current.getIdentificationNumber()))
                throw new DomainException("You can only view transfers for your own accounts.");
        }
        return transferRepo.findBySourceOrDestinationAccount(accountNumber);
    }

    public List<Transfer> getPendingTransfers() {
        AuthService.requireRole(UserRole.COMPANY_SUPERVISOR, UserRole.CLIENT_COMPANY, UserRole.INTERNAL_ANALYST);
        processExpiredTransfers();
        return transferRepo.findByStatus(TransferStatus.PENDING_APPROVAL);
    }

    public List<Transfer> getAllTransfers() {
        AuthService.requireRole(UserRole.INTERNAL_ANALYST);
        return transferRepo.findAll();
    }

    public BigDecimal getApprovalThreshold() {
        return APPROVAL_THRESHOLD.getAmount();
    }

    private Transfer loadTransfer(int transferId) {
        return transferRepo.findById(transferId)
            .orElseThrow(() -> new DomainException("Transfer not found: " + transferId));
    }

    private BankAccount loadAccount(String accountNumber) {
        return accountRepo.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new DomainException("Account not found: " + accountNumber));
    }
}
