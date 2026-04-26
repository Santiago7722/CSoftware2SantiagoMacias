package com.bank.domain.model.aggregate;

import com.bank.domain.exception.DomainValidationException;
import com.bank.domain.exception.InvalidTransferStateException;
import com.bank.domain.model.valueobject.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * DOMAIN AGGREGATE ROOT - Transfer
 *
 * Represents a fund transfer between accounts.
 * Enforces approval flow for high-value company transfers
 * and auto-expiry after 60 minutes without approval.
 */
public class Transfer {

    private Long id;
    private String sourceAccount;
    private String destinationAccount;
    private Money amount;
    private LocalDateTime creationDateTime;
    private LocalDateTime approvalDateTime;
    private TransferStatus status;
    private Long creatorUserId;
    private Long approverUserId;

    private Transfer() {}

    /**
     * Reconstitution factory — restores a Transfer from persistence.
     * Used ONLY by the persistence mapper.
     */
    public static Transfer reconstitute(Long id, String sourceAccount, String destinationAccount,
                                         Money amount, TransferStatus status,
                                         java.time.LocalDateTime creationDateTime,
                                         java.time.LocalDateTime approvalDateTime,
                                         Long creatorUserId, Long approverUserId) {
        Transfer t = new Transfer();
        t.id = id;
        t.sourceAccount = sourceAccount;
        t.destinationAccount = destinationAccount;
        t.amount = amount;
        t.status = status;
        t.creationDateTime = creationDateTime;
        t.approvalDateTime = approvalDateTime;
        t.creatorUserId = creatorUserId;
        t.approverUserId = approverUserId;
        return t;
    }

    /**
     * Factory — creates a transfer that executes immediately (no approval needed).
     */
    public static Transfer createDirect(String sourceAccount, String destinationAccount,
                                         Money amount, Long creatorUserId) {
        Transfer t = buildTransfer(sourceAccount, destinationAccount, amount, creatorUserId);
        t.status = TransferStatus.EXECUTED;
        t.approvalDateTime = t.creationDateTime;
        return t;
    }

    /**
     * Factory — creates a transfer pending supervisor approval.
     */
    public static Transfer createPendingApproval(String sourceAccount, String destinationAccount,
                                                   Money amount, Long creatorUserId) {
        Transfer t = buildTransfer(sourceAccount, destinationAccount, amount, creatorUserId);
        t.status = TransferStatus.PENDING_APPROVAL;
        return t;
    }

    private static Transfer buildTransfer(String sourceAccount, String destinationAccount,
                                           Money amount, Long creatorUserId) {
        if (sourceAccount == null || sourceAccount.isBlank())
            throw new DomainValidationException("Source account is required.");
        if (destinationAccount == null || destinationAccount.isBlank())
            throw new DomainValidationException("Destination account is required.");
        if (amount == null || amount.isZeroOrNegative())
            throw new DomainValidationException("Transfer amount must be greater than zero.");

        Transfer t = new Transfer();
        t.sourceAccount = sourceAccount.trim();
        t.destinationAccount = destinationAccount.trim();
        t.amount = amount;
        t.creationDateTime = LocalDateTime.now();
        t.creatorUserId = creatorUserId;
        return t;
    }

    // ============ Business Rules ============

    public void approve(Long approverUserId) {
        assertPendingApproval();
        checkNotExpired();
        this.approverUserId = approverUserId;
        this.approvalDateTime = LocalDateTime.now();
        this.status = TransferStatus.EXECUTED;
    }

    public void reject(Long approverUserId) {
        assertPendingApproval();
        this.approverUserId = approverUserId;
        this.approvalDateTime = LocalDateTime.now();
        this.status = TransferStatus.REJECTED;
    }

    public void expire() {
        if (this.status != TransferStatus.PENDING_APPROVAL)
            throw new InvalidTransferStateException("Only PENDING_APPROVAL transfers can expire.");
        this.status = TransferStatus.EXPIRED;
    }

    public boolean isExpired() {
        if (status != TransferStatus.PENDING_APPROVAL) return false;
        long minutes = ChronoUnit.MINUTES.between(creationDateTime, LocalDateTime.now());
        return minutes >= 60;
    }

    public boolean isPendingApproval() {
        return this.status == TransferStatus.PENDING_APPROVAL;
    }

    public boolean isExecuted() {
        return this.status == TransferStatus.EXECUTED;
    }

    public long getMinutesPending() {
        return ChronoUnit.MINUTES.between(creationDateTime, LocalDateTime.now());
    }

    private void assertPendingApproval() {
        if (this.status != TransferStatus.PENDING_APPROVAL)
            throw new InvalidTransferStateException(
                    "This action requires PENDING_APPROVAL status. Current: " + this.status);
    }

    private void checkNotExpired() {
        if (isExpired())
            throw new InvalidTransferStateException("Transfer has expired (pending for " + getMinutesPending() + " minutes).");
    }

    // ============ Getters & Setters ============

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSourceAccount() { return sourceAccount; }
    public void setSourceAccount(String sourceAccount) { this.sourceAccount = sourceAccount; }
    public String getDestinationAccount() { return destinationAccount; }
    public void setDestinationAccount(String destinationAccount) { this.destinationAccount = destinationAccount; }
    public Money getAmount() { return amount; }
    public void setAmount(Money amount) { this.amount = amount; }
    public LocalDateTime getCreationDateTime() { return creationDateTime; }
    public void setCreationDateTime(LocalDateTime creationDateTime) { this.creationDateTime = creationDateTime; }
    public LocalDateTime getApprovalDateTime() { return approvalDateTime; }
    public void setApprovalDateTime(LocalDateTime approvalDateTime) { this.approvalDateTime = approvalDateTime; }
    public TransferStatus getStatus() { return status; }
    public void setStatus(TransferStatus status) { this.status = status; }
    public Long getCreatorUserId() { return creatorUserId; }
    public void setCreatorUserId(Long creatorUserId) { this.creatorUserId = creatorUserId; }
    public Long getApproverUserId() { return approverUserId; }
    public void setApproverUserId(Long approverUserId) { this.approverUserId = approverUserId; }
}
