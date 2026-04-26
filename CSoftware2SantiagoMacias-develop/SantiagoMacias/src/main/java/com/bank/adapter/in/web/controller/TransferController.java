package com.bank.adapter.in.web.controller;

import com.bank.application.dto.BankingDto.*;
import com.bank.application.port.input.TransferInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ADAPTER (Driving) - TransferController
 * REST controller for transfer operations including approval flow.
 */
@RestController
@RequestMapping("/api/transfers")
@Tag(name = "Transfers", description = "Fund transfers with approval flow for high-value company transfers")
@SecurityRequirement(name = "bearerAuth")
public class TransferController {

    private final TransferInputPort transferInputPort;

    public TransferController(TransferInputPort transferInputPort) {
        this.transferInputPort = transferInputPort;
    }

    @PostMapping
    @Operation(summary = "Create a transfer",
        description = "Roles: CLIENT_INDIVIDUAL, CLIENT_COMPANY, COMPANY_EMPLOYEE, INTERNAL_ANALYST. " +
            "Company employee transfers above $5,000 go to PENDING_APPROVAL status.")
    public ResponseEntity<ApiResponse<TransferResponse>> createTransfer(
            @Valid @RequestBody CreateTransferCommand command) {
        TransferResponse response = transferInputPort.createTransfer(command);
        return ResponseEntity.status(201).body(ApiResponse.ok("Transfer created", response));
    }

    @GetMapping
    @Operation(summary = "Get all transfers", description = "INTERNAL_ANALYST only")
    public ResponseEntity<ApiResponse<List<TransferResponse>>> getAllTransfers() {
        return ResponseEntity.ok(ApiResponse.ok("Transfers retrieved", transferInputPort.getAllTransfers()));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending transfers (awaiting approval)",
        description = "Roles: COMPANY_SUPERVISOR, CLIENT_COMPANY, INTERNAL_ANALYST. Auto-expires stale transfers.")
    public ResponseEntity<ApiResponse<List<TransferResponse>>> getPendingTransfers() {
        return ResponseEntity.ok(ApiResponse.ok("Pending transfers retrieved", transferInputPort.getPendingTransfers()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transfer by ID")
    public ResponseEntity<ApiResponse<TransferResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Transfer found", transferInputPort.getTransferById(id)));
    }

    @GetMapping("/account/{accountNumber}")
    @Operation(summary = "Get transfers for an account (source or destination)")
    public ResponseEntity<ApiResponse<List<TransferResponse>>> getByAccount(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(ApiResponse.ok("Transfers retrieved",
            transferInputPort.getTransfersByAccount(accountNumber)));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve a pending transfer",
        description = "Roles: COMPANY_SUPERVISOR, CLIENT_COMPANY, INTERNAL_ANALYST")
    public ResponseEntity<ApiResponse<TransferResponse>> approveTransfer(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Transfer approved and executed",
            transferInputPort.approveTransfer(id)));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject a pending transfer",
        description = "Roles: COMPANY_SUPERVISOR, CLIENT_COMPANY, INTERNAL_ANALYST")
    public ResponseEntity<ApiResponse<TransferResponse>> rejectTransfer(
            @PathVariable Long id,
            @Valid @RequestBody ApproveRejectTransferCommand command) {
        return ResponseEntity.ok(ApiResponse.ok("Transfer rejected",
            transferInputPort.rejectTransfer(id, command)));
    }

    @PostMapping("/process-expired")
    @Operation(summary = "Manually trigger expiry check",
        description = "Expires all PENDING_APPROVAL transfers older than 60 minutes. INTERNAL_ANALYST only.")
    public ResponseEntity<ApiResponse<Integer>> processExpired() {
        int count = transferInputPort.processExpiredTransfers();
        return ResponseEntity.ok(ApiResponse.ok("Expired " + count + " transfer(s)", count));
    }
}
