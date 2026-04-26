package com.bank.application.dto;

import com.bank.domain.model.valueobject.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public class BankingDto {

    // ═══════════════════════════════════════════════════════════
    // ACCOUNT DTOs
    // ═══════════════════════════════════════════════════════════

    public record OpenAccountCommand(
        @NotBlank(message = "Owner identification number is required")
        String ownerIdentificationNumber,

        @NotNull(message = "Account type is required")
        AccountType accountType,

        @NotBlank(message = "Currency is required")
        String currency
    ) {}

    public record AccountResponse(
        Long id,
        String accountNumber,
        AccountType accountType,
        String ownerId,
        BigDecimal balance,
        String currency,
        AccountStatus status,
        LocalDate openingDate
    ) {}

    public record DepositWithdrawCommand(
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount
    ) {}

    // ═══════════════════════════════════════════════════════════
    // LOAN DTOs
    // ═══════════════════════════════════════════════════════════

    public record RequestLoanCommand(
        @NotBlank(message = "Loan type is required")
        String loanType,

        @NotNull(message = "Requested amount is required")
        @DecimalMin(value = "0.01", message = "Requested amount must be greater than zero")
        BigDecimal requestedAmount,

        @NotBlank(message = "Currency is required")
        String currency,

        @Min(value = 1, message = "Term must be at least 1 month")
        int termMonths,

        @NotBlank(message = "Disbursement account number is required")
        String disbursementAccountNumber,

        // For bank employees requesting on behalf of a client
        String clientIdentificationNumber
    ) {}

    public record ApproveLoanCommand(
        @NotNull(message = "Approved amount is required")
        @DecimalMin(value = "0.01", message = "Approved amount must be greater than zero")
        BigDecimal approvedAmount,

        @NotBlank(message = "Currency is required")
        String currency,

        @NotNull(message = "Interest rate is required")
        @DecimalMin(value = "0.01", message = "Interest rate must be greater than zero")
        BigDecimal interestRate,

        @Min(value = 1, message = "Term must be at least 1 month")
        int termMonths
    ) {}

    public record RejectLoanCommand(
        @NotBlank(message = "Rejection reason is required")
        String reason
    ) {}

    public record LoanResponse(
        Long id,
        String loanType,
        String clientId,
        BigDecimal requestedAmount,
        BigDecimal approvedAmount,
        BigDecimal interestRate,
        int termMonths,
        LoanStatus status,
        LocalDate approvalDate,
        LocalDate disbursementDate,
        String disbursementAccountNumber,
        String currency
    ) {}

    // ═══════════════════════════════════════════════════════════
    // TRANSFER DTOs
    // ═══════════════════════════════════════════════════════════

    public record CreateTransferCommand(
        @NotBlank(message = "Source account is required")
        String sourceAccount,

        @NotBlank(message = "Destination account is required")
        String destinationAccount,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount
    ) {}

    public record ApproveRejectTransferCommand(
        @NotBlank(message = "Reason is required for rejection")
        String reason
    ) {}

    public record TransferResponse(
        Long id,
        String sourceAccount,
        String destinationAccount,
        BigDecimal amount,
        String currency,
        LocalDateTime creationDateTime,
        LocalDateTime approvalDateTime,
        TransferStatus status,
        Long creatorUserId,
        Long approverUserId
    ) {}

    // ═══════════════════════════════════════════════════════════
    // AUDIT LOG DTOs
    // ═══════════════════════════════════════════════════════════

    public record AuditLogResponse(
        String logId,
        String operationType,
        LocalDateTime operationDateTime,
        Long userId,
        String userRole,
        String affectedProductId,
        Map<String, Object> detailData
    ) {}

    // ═══════════════════════════════════════════════════════════
    // GENERIC RESPONSES
    // ═══════════════════════════════════════════════════════════

    public record ApiResponse<T>(
        boolean success,
        String message,
        T data
    ) {
        public static <T> ApiResponse<T> ok(String message, T data) {
            return new ApiResponse<>(true, message, data);
        }
        public static <T> ApiResponse<T> error(String message) {
            return new ApiResponse<>(false, message, null);
        }
    }
}
