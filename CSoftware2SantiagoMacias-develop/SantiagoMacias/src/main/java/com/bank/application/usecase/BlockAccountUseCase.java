package com.bank.application.usecase;

import com.bank.model.*;
import com.bank.repository.*;
import com.bank.service.AuthService;

/**
 * BLOCK ACCOUNT USE CASE
 * Responsable de bloquear cuentas bancarias
 */
public class BlockAccountUseCase {

    private final AccountRepository accountRepository;
    private final DomainEventPublisher eventPublisher;

    public BlockAccountUseCase(AccountRepository accountRepository,
                               DomainEventPublisher eventPublisher) {
        this.accountRepository = accountRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Bloquear una cuenta
     * @param accountNumber número de cuenta a bloquear
     */
    public void execute(String accountNumber) {
        AuthService.requireRole(UserRole.INTERNAL_ANALYST);

        BankAccount account = findAccountOrThrow(accountNumber);
        account.block("Manual block by analyst");
        
        accountRepository.save(account);
        eventPublisher.publishAll(account.pullDomainEvents());

        System.out.printf("[BLOCK_ACCOUNT] Account %s has been blocked by analyst%n", accountNumber);
    }

    private BankAccount findAccountOrThrow(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new DomainException("Account not found: " + accountNumber));
    }
}
