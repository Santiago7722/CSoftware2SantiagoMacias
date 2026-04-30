package com.bank.service;

import com.bank.application.usecase.CreateAccountUseCase;
import com.bank.model.AccountType;
import com.bank.model.BankAccount;

/**
 * SERVICE - AccountCreationService
 * 
 * Responsable de manejar la creación de cuentas bancarias.
 * Delega la lógica de negocio al caso de uso correspondiente.
 */
public class AccountCreationService {

    private final CreateAccountUseCase createAccountUseCase;

    public AccountCreationService(CreateAccountUseCase createAccountUseCase) {
        this.createAccountUseCase = createAccountUseCase;
    }

    /**
     * Abre una nueva cuenta bancaria
     * @param ownerIdentification identificación del propietario
     * @param accountType tipo de cuenta (SAVINGS, CHECKING)
     * @param currency moneda (USD, EUR, etc.)
     * @return BankAccount la cuenta creada
     */
    public BankAccount openAccount(String ownerIdentification, AccountType accountType, String currency) {
        return createAccountUseCase.execute(ownerIdentification, accountType, currency);
    }
}
