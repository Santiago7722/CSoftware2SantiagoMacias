package com.bank.adapter.out.persistence.entity;

import com.bank.domain.model.valueobject.LoanStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class LoanJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_type", nullable = false)
    private String loanType;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "requested_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal requestedAmount;

    @Column(name = "approved_amount", precision = 19, scale = 4)
    private BigDecimal approvedAmount;

    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "term_months")
    private int termMonths;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Column(name = "disbursement_date")
    private LocalDate disbursementDate;

    @Column(name = "disbursement_account")
    private String disbursementAccount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "creator_user_id", nullable = false)
    private Long creatorUserId;

    @Column(name = "analyst_user_id")
    private Long analystUserId;

    public LoanJpaEntity() {}

    public Long getId()                      { return id; }
    public String getLoanType()              { return loanType; }
    public String getClientId()              { return clientId; }
    public BigDecimal getRequestedAmount()   { return requestedAmount; }
    public BigDecimal getApprovedAmount()    { return approvedAmount; }
    public BigDecimal getInterestRate()      { return interestRate; }
    public int getTermMonths()               { return termMonths; }
    public LoanStatus getStatus()            { return status; }
    public LocalDate getApprovalDate()       { return approvalDate; }
    public LocalDate getDisbursementDate()   { return disbursementDate; }
    public String getDisbursementAccount()   { return disbursementAccount; }
    public String getCurrency()              { return currency; }
    public Long getCreatorUserId()           { return creatorUserId; }
    public Long getAnalystUserId()           { return analystUserId; }

    public void setId(Long v)                      { this.id = v; }
    public void setLoanType(String v)              { this.loanType = v; }
    public void setClientId(String v)              { this.clientId = v; }
    public void setRequestedAmount(BigDecimal v)   { this.requestedAmount = v; }
    public void setApprovedAmount(BigDecimal v)    { this.approvedAmount = v; }
    public void setInterestRate(BigDecimal v)      { this.interestRate = v; }
    public void setTermMonths(int v)               { this.termMonths = v; }
    public void setStatus(LoanStatus v)            { this.status = v; }
    public void setApprovalDate(LocalDate v)       { this.approvalDate = v; }
    public void setDisbursementDate(LocalDate v)   { this.disbursementDate = v; }
    public void setDisbursementAccount(String v)   { this.disbursementAccount = v; }
    public void setCurrency(String v)              { this.currency = v; }
    public void setCreatorUserId(Long v)           { this.creatorUserId = v; }
    public void setAnalystUserId(Long v)           { this.analystUserId = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id, creatorUserId, analystUserId;
        private String loanType, clientId, disbursementAccount, currency;
        private BigDecimal requestedAmount, approvedAmount, interestRate;
        private int termMonths;
        private LoanStatus status;
        private LocalDate approvalDate, disbursementDate;
        public Builder id(Long v)                      { this.id = v; return this; }
        public Builder loanType(String v)              { this.loanType = v; return this; }
        public Builder clientId(String v)              { this.clientId = v; return this; }
        public Builder requestedAmount(BigDecimal v)   { this.requestedAmount = v; return this; }
        public Builder approvedAmount(BigDecimal v)    { this.approvedAmount = v; return this; }
        public Builder interestRate(BigDecimal v)      { this.interestRate = v; return this; }
        public Builder termMonths(int v)               { this.termMonths = v; return this; }
        public Builder status(LoanStatus v)            { this.status = v; return this; }
        public Builder approvalDate(LocalDate v)       { this.approvalDate = v; return this; }
        public Builder disbursementDate(LocalDate v)   { this.disbursementDate = v; return this; }
        public Builder disbursementAccount(String v)   { this.disbursementAccount = v; return this; }
        public Builder currency(String v)              { this.currency = v; return this; }
        public Builder creatorUserId(Long v)           { this.creatorUserId = v; return this; }
        public Builder analystUserId(Long v)           { this.analystUserId = v; return this; }
        public LoanJpaEntity build() {
            LoanJpaEntity e = new LoanJpaEntity();
            e.id = id; e.loanType = loanType; e.clientId = clientId;
            e.requestedAmount = requestedAmount; e.approvedAmount = approvedAmount;
            e.interestRate = interestRate; e.termMonths = termMonths; e.status = status;
            e.approvalDate = approvalDate; e.disbursementDate = disbursementDate;
            e.disbursementAccount = disbursementAccount; e.currency = currency;
            e.creatorUserId = creatorUserId; e.analystUserId = analystUserId;
            return e;
        }
    }
}
