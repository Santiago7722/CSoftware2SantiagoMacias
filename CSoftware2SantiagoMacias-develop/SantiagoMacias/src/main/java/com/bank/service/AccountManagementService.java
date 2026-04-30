package com.bank.service;

import com.bank.application.usecase.BlockAccountUseCase;

/**
 * SERVICE - AccountManagementService
 * 
 * Responsable de manejar la administración de cuentas (bloqueo, etc.).
 * Delega la lógica de negocio al caso de uso correspondiente.
 */
public class AccountManagementService {

    private final BlockAccountUseCase blockAccountUseCase;

    public AccountManagementService(BlockAccountUseCase blockAccountUseCase) {
        this.blockAccountUseCase = blockAccountUseCase;
    }

    /**
     * Bloquea una cuenta
     * @param accountNumber número de cuenta a bloquear
     */
    public void blockAccount(String accountNumber) {
        blockAccountUseCase.execute(accountNumber);
    }
}
