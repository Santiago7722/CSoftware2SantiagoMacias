package com.bank.application.usecase;

import com.bank.model.*;
import com.bank.repository.*;
import com.bank.service.AuthService;

/**
 * DEPOSIT AND WITHDRAW USE CASE
 * Responsable de depósitos y retiros de cuentas bancarias
 */
public class DepositWithdrawUseCase {

    private final AccountRepository accountRepository;
    private final DomainEventPublisher eventPublisher;

    public DepositWithdrawUseCase(AccountRepository accountRepository,
                                   DomainEventPublisher eventPublisher) {
        this.accountRepository = accountRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Realizar depósito a una cuenta
     * @param accountNumber número de cuenta destino
     * @param amount monto a depositar
     */
    public void deposit(String accountNumber, java.math.BigDecimal amount) {
        AuthService.requireRole(UserRole.TELLER, UserRole.INTERNAL_ANALYST);

        BankAccount account = findAccountOrThrow(accountNumber);
        Money money = new Money(amount, account.getBalance().getCurrency());
        
        account.deposit(money);
        accountRepository.save(account);
        eventPublisher.publishAll(account.pullDomainEvents());

        System.out.printf("[DEPOSIT] Amount %.2f deposited to %s. New balance: %s%n",
                amount, accountNumber, account.getBalance());
    }

    /**
     * Realizar retiro de una cuenta
     * @param accountNumber número de cuenta origen
     * @param amount monto a retirar
     */
    public void withdraw(String accountNumber, java.math.BigDecimal amount) {
        AuthService.requireRole(UserRole.TELLER, UserRole.INTERNAL_ANALYST);

        BankAccount account = findAccountOrThrow(accountNumber);
        Money money = new Money(amount, account.getBalance().getCurrency());
        
        account.withdraw(money);
        accountRepository.save(account);
        eventPublisher.publishAll(account.pullDomainEvents());

        System.out.printf("[WITHDRAW] Amount %.2f withdrawn from %s. New balance: %s%n",
                amount, accountNumber, account.getBalance());
    }

    private BankAccount findAccountOrThrow(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new DomainException("Account not found: " + accountNumber));
    }
}
