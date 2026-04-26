package com.bank.service;

import com.bank.model.*;
import com.bank.repository.*;
import com.bank.application.usecase.*;

import java.util.List;
import java.util.Optional;

/**
 * FACADE SERVICE — Orquestador de operaciones de cuentas (DDD)
 * 
 * Esta clase actúa como Facade que delega a UseCase especializados:
 * - CreateAccountUseCase: Creación de cuentas
 * - DepositWithdrawUseCase: Depósitos y retiros
 * - BlockAccountUseCase: Bloqueo de cuentas
 * - RetrieveAccountUseCase: Consultas de cuentas
 * 
 * Mantiene compatibilidad con la interfaz anterior.
 */
public class AccountService {

    private final CreateAccountUseCase createAccountUseCase;
    private final DepositWithdrawUseCase depositWithdrawUseCase;
    private final BlockAccountUseCase blockAccountUseCase;
    private final RetrieveAccountUseCase retrieveAccountUseCase;

    public AccountService() {
        AccountRepository accountRepo = new SqliteAccountRepository();
        UserRepository userRepo = new SqliteUserRepository();
        DomainEventPublisher eventPublisher = new AuditLogRepository();

        this.createAccountUseCase = new CreateAccountUseCase(accountRepo, userRepo, eventPublisher);
        this.depositWithdrawUseCase = new DepositWithdrawUseCase(accountRepo, eventPublisher);
        this.blockAccountUseCase = new BlockAccountUseCase(accountRepo, eventPublisher);
        this.retrieveAccountUseCase = new RetrieveAccountUseCase(accountRepo);
    }

    /**
     * Inyección de dependencias para testing
     * @param createAccountUseCase caso de uso de creación de cuentas
     * @param depositWithdrawUseCase caso de uso de depósitos y retiros
     * @param blockAccountUseCase caso de uso de bloqueo de cuentas
     * @param retrieveAccountUseCase caso de uso de consulta de cuentas
     */
    public AccountService(CreateAccountUseCase createAccountUseCase,
                          DepositWithdrawUseCase depositWithdrawUseCase,
                          BlockAccountUseCase blockAccountUseCase,
                          RetrieveAccountUseCase retrieveAccountUseCase) {
        this.createAccountUseCase = createAccountUseCase;
        this.depositWithdrawUseCase = depositWithdrawUseCase;
        this.blockAccountUseCase = blockAccountUseCase;
        this.retrieveAccountUseCase = retrieveAccountUseCase;
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

    /**
     * Realiza un depósito a una cuenta
     * @param accountNumber número de cuenta destino
     * @param amount monto a depositar
     */
    public void deposit(String accountNumber, java.math.BigDecimal amount) {
        depositWithdrawUseCase.deposit(accountNumber, amount);
    }

    /**
     * Realiza un retiro de una cuenta
     * @param accountNumber número de cuenta origen
     * @param amount monto a retirar
     */
    public void withdraw(String accountNumber, java.math.BigDecimal amount) {
        depositWithdrawUseCase.withdraw(accountNumber, amount);
    }

    /**
     * Bloquea una cuenta
     * @param accountNumber número de cuenta a bloquear
     */
    public void blockAccount(String accountNumber) {
        blockAccountUseCase.execute(accountNumber);
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
