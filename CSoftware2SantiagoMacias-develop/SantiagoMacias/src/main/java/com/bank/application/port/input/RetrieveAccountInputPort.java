package com.bank.application.port.input;

import com.bank.model.BankAccount;
import java.util.List;
import java.util.Optional;

/**
 * INPUT PORT - Retrieve Account
 * Define el contrato para consultar cuentas
 */
public interface RetrieveAccountInputPort {
    Optional<BankAccount> getAccountByNumber(String accountNumber);
    List<BankAccount> getAccountsByOwner(String ownerIdentification);
    List<BankAccount> getAllAccounts();
    BankAccount loadAccount(String accountNumber);
}
