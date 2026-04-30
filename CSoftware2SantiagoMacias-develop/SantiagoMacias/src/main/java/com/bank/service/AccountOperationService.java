package com.bank.service;

import com.bank.application.usecase.DepositWithdrawUseCase;
import java.math.BigDecimal;

/**
 * SERVICE - AccountOperationService
 * 
 * Responsable de manejar operaciones de depósitos y retiros.
 * Delega la lógica de negocio al caso de uso correspondiente.
 */
public class AccountOperationService {

    private final DepositWithdrawUseCase depositWithdrawUseCase;

    public AccountOperationService(DepositWithdrawUseCase depositWithdrawUseCase) {
        this.depositWithdrawUseCase = depositWithdrawUseCase;
    }

    /**
     * Realiza un depósito a una cuenta
     * @param accountNumber número de cuenta destino
     * @param amount monto a depositar
     */
    public void deposit(String accountNumber, BigDecimal amount) {
        depositWithdrawUseCase.deposit(accountNumber, amount);
    }

    /**
     * Realiza un retiro de una cuenta
     * @param accountNumber número de cuenta origen
     * @param amount monto a retirar
     */
    public void withdraw(String accountNumber, BigDecimal amount) {
        depositWithdrawUseCase.withdraw(accountNumber, amount);
    }
}
