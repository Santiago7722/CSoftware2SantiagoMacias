package com.bank.adapter.out.persistence.entity;

import com.bank.domain.model.valueobject.TransferStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfers")
public class TransferJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_account", nullable = false)
    private String sourceAccount;

    @Column(name = "destination_account", nullable = false)
    private String destinationAccount;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "creation_date_time", nullable = false)
    private LocalDateTime creationDateTime;

    @Column(name = "approval_date_time")
    private LocalDateTime approvalDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;

    @Column(name = "creator_user_id", nullable = false)
    private Long creatorUserId;

    @Column(name = "approver_user_id")
    private Long approverUserId;

    public TransferJpaEntity() {}

    public Long getId()                        { return id; }
    public String getSourceAccount()           { return sourceAccount; }
    public String getDestinationAccount()      { return destinationAccount; }
    public BigDecimal getAmount()              { return amount; }
    public String getCurrency()                { return currency; }
    public LocalDateTime getCreationDateTime() { return creationDateTime; }
    public LocalDateTime getApprovalDateTime() { return approvalDateTime; }
    public TransferStatus getStatus()          { return status; }
    public Long getCreatorUserId()             { return creatorUserId; }
    public Long getApproverUserId()            { return approverUserId; }

    public void setId(Long v)                        { this.id = v; }
    public void setSourceAccount(String v)           { this.sourceAccount = v; }
    public void setDestinationAccount(String v)      { this.destinationAccount = v; }
    public void setAmount(BigDecimal v)              { this.amount = v; }
    public void setCurrency(String v)                { this.currency = v; }
    public void setCreationDateTime(LocalDateTime v) { this.creationDateTime = v; }
    public void setApprovalDateTime(LocalDateTime v) { this.approvalDateTime = v; }
    public void setStatus(TransferStatus v)          { this.status = v; }
    public void setCreatorUserId(Long v)             { this.creatorUserId = v; }
    public void setApproverUserId(Long v)            { this.approverUserId = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id, creatorUserId, approverUserId;
        private String sourceAccount, destinationAccount, currency;
        private BigDecimal amount;
        private LocalDateTime creationDateTime, approvalDateTime;
        private TransferStatus status;
        public Builder id(Long v)                        { this.id = v; return this; }
        public Builder sourceAccount(String v)           { this.sourceAccount = v; return this; }
        public Builder destinationAccount(String v)      { this.destinationAccount = v; return this; }
        public Builder amount(BigDecimal v)              { this.amount = v; return this; }
        public Builder currency(String v)                { this.currency = v; return this; }
        public Builder creationDateTime(LocalDateTime v) { this.creationDateTime = v; return this; }
        public Builder approvalDateTime(LocalDateTime v) { this.approvalDateTime = v; return this; }
        public Builder status(TransferStatus v)          { this.status = v; return this; }
        public Builder creatorUserId(Long v)             { this.creatorUserId = v; return this; }
        public Builder approverUserId(Long v)            { this.approverUserId = v; return this; }
        public TransferJpaEntity build() {
            TransferJpaEntity e = new TransferJpaEntity();
            e.id = id; e.sourceAccount = sourceAccount; e.destinationAccount = destinationAccount;
            e.amount = amount; e.currency = currency; e.creationDateTime = creationDateTime;
            e.approvalDateTime = approvalDateTime; e.status = status;
            e.creatorUserId = creatorUserId; e.approverUserId = approverUserId;
            return e;
        }
    }
}
