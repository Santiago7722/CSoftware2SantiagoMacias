package com.bank.application.port.input;

import com.bank.model.AccountType;
import com.bank.model.BankAccount;

/**
 * INPUT PORT - Create Account
 * Define el contrato para la creación de cuentas
 */
public interface CreateAccountInputPort {
    BankAccount openAccount(String ownerIdentification, AccountType accountType, String currency);
}
