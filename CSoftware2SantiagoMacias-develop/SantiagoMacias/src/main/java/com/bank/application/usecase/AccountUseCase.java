package com.bank.application.usecase;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.application.dto.BankingDto.*;
import com.bank.application.dto.BankingDto.AccountResponse;
import com.bank.application.dto.BankingDto.DepositWithdrawCommand;
import com.bank.application.dto.BankingDto.OpenAccountCommand;
import com.bank.application.port.input.AccountInputPort;
import com.bank.application.port.output.AuditLogRepositoryPort;
import com.bank.application.port.output.BankAccountRepositoryPort;
import com.bank.application.port.output.UserRepositoryPort;
import com.bank.domain.exception.AccessDeniedException;
import com.bank.domain.exception.DomainValidationException;
import com.bank.domain.exception.ResourceNotFoundException;
import com.bank.domain.model.aggregate.BankAccount;
import com.bank.domain.model.entity.AuditLog;
import com.bank.domain.model.entity.User;
import com.bank.domain.model.valueobject.Money;
import com.bank.domain.model.valueobject.UserRole;
import com.bank.shared.AccountNumberGenerator;
import com.bank.shared.SecurityContextHelper;

/**
 * APPLICATION USE CASE - AccountUseCase
 *
 * Orchestrates bank account operations (open, deposit, withdraw, block).
 * Delegates business rule enforcement to the BankAccount aggregate.
 */
@Service
@Transactional
public class AccountUseCase implements AccountInputPort {

    private final BankAccountRepositoryPort accountRepository;
    private final UserRepositoryPort userRepository;
    private final AuditLogRepositoryPort auditLogRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    public AccountUseCase(BankAccountRepositoryPort accountRepository,
                          UserRepositoryPort userRepository,
                          AuditLogRepositoryPort auditLogRepository,
                          AccountNumberGenerator accountNumberGenerator) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
        this.accountNumberGenerator = accountNumberGenerator;
    }

    @Override
    public AccountResponse openAccount(OpenAccountCommand command) {
        SecurityContextHelper.requireAnyRole(
            UserRole.TELLER, UserRole.COMMERCIAL_EMPLOYEE, UserRole.INTERNAL_ANALYST,
            UserRole.CLIENT_INDIVIDUAL, UserRole.CLIENT_COMPANY
        );
        User current = SecurityContextHelper.getCurrentUser();

        // Clients can only open accounts for themselves
        String ownerIdToUse = command.ownerIdentificationNumber();
        if (current.getRole() == UserRole.CLIENT_INDIVIDUAL || current.getRole() == UserRole.CLIENT_COMPANY) {
            ownerIdToUse = current.getIdentificationNumber();
        }

        final String finalOwnerId = ownerIdToUse;
        User owner = userRepository.findByIdentificationNumber(finalOwnerId)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + finalOwnerId));

        if (!owner.isActive())
            throw new DomainValidationException("Cannot open account for a non-ACTIVE client. Status: " + owner.getStatus());

        String accountNumber = accountNumberGenerator.generate(accountRepository::existsByAccountNumber);
        BankAccount account = BankAccount.open(accountNumber, command.accountType(), finalOwnerId, command.currency());
        BankAccount saved = accountRepository.save(account);

        audit("ACCOUNT_OPENED", accountNumber, Map.of(
            "accountType", command.accountType().name(),
            "ownerId", finalOwnerId,
            "currency", command.currency()
        ));
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccount(String accountNumber) {
        SecurityContextHelper.requireLogin();
        BankAccount account = findAccountOrThrow(accountNumber);
        assertCanAccessAccount(account);
        return toResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByOwner(String ownerIdentificationNumber) {
        SecurityContextHelper.requireLogin();
        User current = SecurityContextHelper.getCurrentUser();
        if (current.getRole() == UserRole.CLIENT_INDIVIDUAL) {
            if (!current.getIdentificationNumber().equals(ownerIdentificationNumber))
                throw new AccessDeniedException("You can only view your own accounts.");
        }
        return accountRepository.findByOwnerId(ownerIdentificationNumber)
            .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        SecurityContextHelper.requireAnyRole(UserRole.INTERNAL_ANALYST, UserRole.TELLER, UserRole.COMMERCIAL_EMPLOYEE);
        return accountRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public AccountResponse deposit(String accountNumber, DepositWithdrawCommand command) {
        SecurityContextHelper.requireAnyRole(UserRole.TELLER, UserRole.INTERNAL_ANALYST);
        BankAccount account = findAccountOrThrow(accountNumber);
        BigDecimal before = account.getBalance().getAmount();
        account.deposit(new Money(command.amount(), account.getBalance().getCurrency()));
        BankAccount saved = accountRepository.save(account);
        audit("DEPOSIT", accountNumber, Map.of(
            "amount", command.amount(),
            "balanceBefore", before,
            "balanceAfter", saved.getBalance().getAmount()
        ));
        return toResponse(saved);
    }

    @Override
    public AccountResponse withdraw(String accountNumber, DepositWithdrawCommand command) {
        SecurityContextHelper.requireAnyRole(UserRole.TELLER, UserRole.INTERNAL_ANALYST);
        BankAccount account = findAccountOrThrow(accountNumber);
        BigDecimal before = account.getBalance().getAmount();
        account.withdraw(new Money(command.amount(), account.getBalance().getCurrency()));
        BankAccount saved = accountRepository.save(account);
        audit("WITHDRAWAL", accountNumber, Map.of(
            "amount", command.amount(),
            "balanceBefore", before,
            "balanceAfter", saved.getBalance().getAmount()
        ));
        return toResponse(saved);
    }

    @Override
    public AccountResponse blockAccount(String accountNumber) {
        SecurityContextHelper.requireAnyRole(UserRole.INTERNAL_ANALYST);
        BankAccount account = findAccountOrThrow(accountNumber);
        account.block();
        BankAccount saved = accountRepository.save(account);
        audit("ACCOUNT_BLOCKED", accountNumber, Map.of("reason", "Manual block by analyst"));
        return toResponse(saved);
    }

    @Override
    public AccountResponse unblockAccount(String accountNumber) {
        SecurityContextHelper.requireAnyRole(UserRole.INTERNAL_ANALYST);
        BankAccount account = findAccountOrThrow(accountNumber);
        account.unblock();
        BankAccount saved = accountRepository.save(account);
        audit("ACCOUNT_UNBLOCKED", accountNumber, Map.of("action", "Manual unblock by analyst"));
        return toResponse(saved);
    }

    // ─── Helpers ────────────────────────────────────────────────────────

    public BankAccount findAccountOrThrow(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
    }

    private void assertCanAccessAccount(BankAccount account) {
        User current = SecurityContextHelper.getCurrentUser();
        if (current.getRole() == UserRole.CLIENT_INDIVIDUAL
            && !account.belongsTo(current.getIdentificationNumber())) {
            throw new AccessDeniedException("You can only access your own accounts.");
        }
    }

    private void audit(String op, String productId, Map<String, Object> detail) {
        User current = SecurityContextHelper.getCurrentUser();
        if (current == null) return;
        auditLogRepository.save(AuditLog.record(op, current.getId(), current.getRole().name(), productId, detail));
    }

    AccountResponse toResponse(BankAccount a) {
        return new AccountResponse(
            a.getId(), a.getAccountNumber(), a.getAccountType(), a.getOwnerId(),
            a.getBalance().getAmount(), a.getBalance().getCurrency(), a.getStatus(), a.getOpeningDate()
        );
    }
}
