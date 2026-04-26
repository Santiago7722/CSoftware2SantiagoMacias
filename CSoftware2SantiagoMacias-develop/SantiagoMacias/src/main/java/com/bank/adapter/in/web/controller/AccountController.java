package com.bank.adapter.in.web.controller;

import com.bank.application.dto.BankingDto.*;
import com.bank.application.port.input.AccountInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ADAPTER (Driving) - AccountController
 * REST controller for bank account operations.
 */
@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "Bank account operations")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private final AccountInputPort accountInputPort;

    public AccountController(AccountInputPort accountInputPort) {
        this.accountInputPort = accountInputPort;
    }

    @PostMapping
    @Operation(summary = "Open a new account",
        description = "Roles: TELLER, COMMERCIAL_EMPLOYEE, INTERNAL_ANALYST, CLIENT_INDIVIDUAL, CLIENT_COMPANY")
    public ResponseEntity<ApiResponse<AccountResponse>> openAccount(
            @Valid @RequestBody OpenAccountCommand command) {
        return ResponseEntity.status(201)
            .body(ApiResponse.ok("Account opened successfully", accountInputPort.openAccount(command)));
    }

    @GetMapping
    @Operation(summary = "Get all accounts", description = "Roles: INTERNAL_ANALYST, TELLER, COMMERCIAL_EMPLOYEE")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAllAccounts() {
        return ResponseEntity.ok(ApiResponse.ok("Accounts retrieved", accountInputPort.getAllAccounts()));
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get account by number")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(ApiResponse.ok("Account found", accountInputPort.getAccount(accountNumber)));
    }

    @GetMapping("/owner/{ownerIdentificationNumber}")
    @Operation(summary = "Get accounts by owner identification number")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getByOwner(
            @PathVariable String ownerIdentificationNumber) {
        return ResponseEntity.ok(ApiResponse.ok("Accounts retrieved",
            accountInputPort.getAccountsByOwner(ownerIdentificationNumber)));
    }

    @PostMapping("/{accountNumber}/deposit")
    @Operation(summary = "Deposit money", description = "Roles: TELLER, INTERNAL_ANALYST")
    public ResponseEntity<ApiResponse<AccountResponse>> deposit(
            @PathVariable String accountNumber,
            @Valid @RequestBody DepositWithdrawCommand command) {
        return ResponseEntity.ok(ApiResponse.ok("Deposit successful",
            accountInputPort.deposit(accountNumber, command)));
    }

    @PostMapping("/{accountNumber}/withdraw")
    @Operation(summary = "Withdraw money", description = "Roles: TELLER, INTERNAL_ANALYST")
    public ResponseEntity<ApiResponse<AccountResponse>> withdraw(
            @PathVariable String accountNumber,
            @Valid @RequestBody DepositWithdrawCommand command) {
        return ResponseEntity.ok(ApiResponse.ok("Withdrawal successful",
            accountInputPort.withdraw(accountNumber, command)));
    }

    @PatchMapping("/{accountNumber}/block")
    @Operation(summary = "Block an account", description = "INTERNAL_ANALYST only")
    public ResponseEntity<ApiResponse<AccountResponse>> blockAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(ApiResponse.ok("Account blocked", accountInputPort.blockAccount(accountNumber)));
    }

    @PatchMapping("/{accountNumber}/unblock")
    @Operation(summary = "Unblock an account", description = "INTERNAL_ANALYST only")
    public ResponseEntity<ApiResponse<AccountResponse>> unblockAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(ApiResponse.ok("Account unblocked", accountInputPort.unblockAccount(accountNumber)));
    }
}
