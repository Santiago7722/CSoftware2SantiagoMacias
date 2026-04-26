package com.bank.application.usecase;

import com.bank.model.*;
import com.bank.repository.*;
import com.bank.service.AuthService;
import com.bank.util.AccountNumberGenerator;

/**
 * CREATE ACCOUNT USE CASE
 * Responsable de la creación de cuentas bancarias
 */
public class CreateAccountUseCase {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final DomainEventPublisher eventPublisher;

    public CreateAccountUseCase(AccountRepository accountRepository,
                                UserRepository userRepository,
                                DomainEventPublisher eventPublisher) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    public BankAccount execute(String ownerIdentification, AccountType accountType, String currency) {
        // Validar permiso de rol
        AuthService.requireRole(
            UserRole.TELLER, UserRole.COMMERCIAL_EMPLOYEE, UserRole.INTERNAL_ANALYST,
            UserRole.CLIENT_INDIVIDUAL, UserRole.CLIENT_COMPANY
        );
        User current = AuthService.getCurrentUser();

        // Validar que clientes solo abran cuentas para sí mismos
        if (current.hasRole(UserRole.CLIENT_INDIVIDUAL, UserRole.CLIENT_COMPANY)) {
            if (!current.isOwner(ownerIdentification))
                throw new DomainException("Clients can only open accounts for themselves.");
        }

        // Obtener y validar el propietario
        User owner = userRepository.findByIdentification(ownerIdentification)
            .orElseThrow(() -> new DomainException("Client not found: " + ownerIdentification));

        if (!owner.isOperational())
            throw new DomainException("Cannot open account for an INACTIVE or BLOCKED client.");

        // Generar número único de cuenta
        String accountNumber = new AccountNumberGenerator().generate(accountRepository);

        // Crear y guardar la cuenta
        BankAccount account = BankAccount.open(accountNumber, ownerIdentification, accountType, currency);
        accountRepository.save(account);
        eventPublisher.publishAll(account.pullDomainEvents());

        System.out.printf("[CREATE_ACCOUNT] New account %s opened for client %s (Type: %s, Currency: %s)%n",
                accountNumber, ownerIdentification, accountType, currency);

        return account;
    }
}
