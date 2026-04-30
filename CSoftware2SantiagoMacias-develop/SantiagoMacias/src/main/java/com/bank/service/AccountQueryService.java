package com.bank.service;

import com.bank.application.usecase.RetrieveAccountUseCase;
import com.bank.model.BankAccount;
import java.util.List;
import java.util.Optional;

/**
 * SERVICE - AccountQueryService
 * 
 * Responsable de manejar consultas y búsquedas de cuentas.
 * Delega la lógica de negocio al caso de uso correspondiente.
 */
public class AccountQueryService {

    private final RetrieveAccountUseCase retrieveAccountUseCase;

    public AccountQueryService(RetrieveAccountUseCase retrieveAccountUseCase) {
        this.retrieveAccountUseCase = retrieveAccountUseCase;
    }

    /**
     * Obtiene una cuenta por número de cuenta
     * @param accountNumber número de cuenta
     * @return Optional con la cuenta si existe
     */
    public Optional<BankAccount> getAccount(String accountNumber) {
        return retrieveAccountUseCase.getAccountByNumber(accountNumber);
    }

    /**
     * Obtiene todas las cuentas de un propietario
     * @param ownerIdentification identificación del propietario
     * @return Lista de cuentas del propietario
     */
    public List<BankAccount> getAccountsByOwner(String ownerIdentification) {
        return retrieveAccountUseCase.getAccountsByOwner(ownerIdentification);
    }

    /**
     * Obtiene todas las cuentas del sistema
     * @return Lista de todas las cuentas
     */
    public List<BankAccount> getAllAccounts() {
        return retrieveAccountUseCase.getAllAccounts();
    }

    /**
     * Carga una cuenta por número (lanza excepción si no existe)
     * @param accountNumber número de cuenta
     * @return BankAccount la cuenta solicitada
     */
    public BankAccount loadAccount(String accountNumber) {
        return retrieveAccountUseCase.loadAccount(accountNumber);
    }
}
