package com.bank.application.usecase;

import com.bank.model.*;
import com.bank.repository.*;
import com.bank.service.AuthService;
import java.util.List;
import java.util.Optional;

/**
 * RETRIEVE ACCOUNT USE CASE
 * Responsable de consultar información de cuentas bancarias
 */
public class RetrieveAccountUseCase {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public RetrieveAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = null;
    }

    /**
     * Obtener una cuenta específica por número de cuenta
     * @param accountNumber número de cuenta
     * @return Optional con la cuenta si existe
     */
    public Optional<BankAccount> getAccountByNumber(String accountNumber) {
        AuthService.requireLogin();
        return accountRepository.findByAccountNumber(accountNumber);
    }

    /**
     * Obtener todas las cuentas de un propietario
     * @param ownerIdentification identificación del propietario
     * @return Lista de cuentas del propietario
     */
    public List<BankAccount> getAccountsByOwner(String ownerIdentification) {
        AuthService.requireLogin();
        User current = AuthService.getCurrentUser();

        // Los clientes solo pueden ver sus propias cuentas
        if (current.hasRole(UserRole.CLIENT_INDIVIDUAL, UserRole.CLIENT_COMPANY)) {
            if (!current.isOwner(ownerIdentification))
                throw new DomainException("You can only view your own accounts.");
        }

        return accountRepository.findByOwnerId(ownerIdentification);
    }

    /**
     * Obtener todas las cuentas (solo analistas, tellers, empleados comerciales)
     * @return Lista de todas las cuentas
     */
    public List<BankAccount> getAllAccounts() {
        AuthService.requireRole(UserRole.INTERNAL_ANALYST, UserRole.TELLER, UserRole.COMMERCIAL_EMPLOYEE);
        return accountRepository.findAll();
    }

    /**
     * Obtener una cuenta o lanzar excepción
     * @param accountNumber número de cuenta
     * @return BankAccount la cuenta solicitada
     */
    public BankAccount loadAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new DomainException("Account not found: " + accountNumber));
    }
}
