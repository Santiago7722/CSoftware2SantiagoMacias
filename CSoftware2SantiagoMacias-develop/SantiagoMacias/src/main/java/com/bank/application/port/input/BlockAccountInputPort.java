package com.bank.application.port.input;

/**
 * INPUT PORT - Block Account
 * Define el contrato para bloquear cuentas
 */
public interface BlockAccountInputPort {
    void blockAccount(String accountNumber);
}
