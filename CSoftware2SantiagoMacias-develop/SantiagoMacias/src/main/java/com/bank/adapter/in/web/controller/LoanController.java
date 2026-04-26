package com.bank.adapter.in.web.controller;

import com.bank.application.dto.BankingDto.*;
import com.bank.application.port.input.LoanInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ADAPTER (Driving) - LoanController
 * REST controller for loan lifecycle operations.
 */
@RestController
@RequestMapping("/api/loans")
@Tag(name = "Loans", description = "Loan request, approval, and disbursement flow")
@SecurityRequirement(name = "bearerAuth")
public class LoanController {

    private final LoanInputPort loanInputPort;

    public LoanController(LoanInputPort loanInputPort) {
        this.loanInputPort = loanInputPort;
    }

    @PostMapping
    @Operation(summary = "Request a loan",
        description = "Roles: CLIENT_INDIVIDUAL, CLIENT_COMPANY, COMMERCIAL_EMPLOYEE, INTERNAL_ANALYST. " +
            "Employees must provide clientIdentificationNumber.")
    public ResponseEntity<ApiResponse<LoanResponse>> requestLoan(
            @Valid @RequestBody RequestLoanCommand command) {
        return ResponseEntity.status(201)
            .body(ApiResponse.ok("Loan request submitted", loanInputPort.requestLoan(command)));
    }

    @GetMapping
    @Operation(summary = "Get all loans", description = "Roles: INTERNAL_ANALYST, COMMERCIAL_EMPLOYEE")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getAllLoans() {
        return ResponseEntity.ok(ApiResponse.ok("Loans retrieved", loanInputPort.getAllLoans()));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get loans by status",
        description = "Status values: UNDER_REVIEW, APPROVED, REJECTED, DISBURSED")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(ApiResponse.ok("Loans retrieved", loanInputPort.getLoansByStatus(status)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get loan by ID")
    public ResponseEntity<ApiResponse<LoanResponse>> getLoanById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Loan found", loanInputPort.getLoanById(id)));
    }

    @GetMapping("/client/{clientIdentificationNumber}")
    @Operation(summary = "Get loans by client identification number")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getByClient(
            @PathVariable String clientIdentificationNumber) {
        return ResponseEntity.ok(ApiResponse.ok("Loans retrieved",
            loanInputPort.getLoansByClient(clientIdentificationNumber)));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve a loan", description = "INTERNAL_ANALYST only")
    public ResponseEntity<ApiResponse<LoanResponse>> approveLoan(
            @PathVariable Long id,
            @Valid @RequestBody ApproveLoanCommand command) {
        return ResponseEntity.ok(ApiResponse.ok("Loan approved", loanInputPort.approveLoan(id, command)));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject a loan", description = "INTERNAL_ANALYST only")
    public ResponseEntity<ApiResponse<LoanResponse>> rejectLoan(
            @PathVariable Long id,
            @Valid @RequestBody RejectLoanCommand command) {
        return ResponseEntity.ok(ApiResponse.ok("Loan rejected", loanInputPort.rejectLoan(id, command)));
    }

    @PostMapping("/{id}/disburse")
    @Operation(summary = "Disburse an approved loan", description = "INTERNAL_ANALYST only")
    public ResponseEntity<ApiResponse<LoanResponse>> disburseLoan(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Loan disbursed", loanInputPort.disburseLoan(id)));
    }
}
