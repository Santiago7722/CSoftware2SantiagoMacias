package com.bank.service;

import java.util.List;
import java.util.Optional;

import com.bank.application.usecase.BlockAccountUseCase;
import com.bank.application.usecase.CreateAccountUseCase;
import com.bank.application.usecase.DepositWithdrawUseCase;
import com.bank.application.usecase.RetrieveAccountUseCase;
import com.bank.model.AccountType;
import com.bank.model.BankAccount;
import com.bank.repository.AccountRepository;
import com.bank.repository.AuditLogRepository;
import com.bank.repository.DomainEventPublisher;
import com.bank.repository.SqliteAccountRepository;
import com.bank.repository.SqliteUserRepository;
import com.bank.repository.UserRepository;

/**
 * FACADE SERVICE — Orquestador de operaciones de cuentas (DDD)
 * 
 * Esta clase actúa como Facade que coordina servicios especializados:
 * - AccountCreationService: Creación de cuentas
 * - AccountOperationService: Depósitos y retiros
 * - AccountManagementService: Bloqueo de cuentas
 * - AccountQueryService: Consultas de cuentas
 * 
 * Mantiene compatibilidad con la interfaz anterior.
 */
public class AccountService {

    private final AccountCreationService creationService;
    private final AccountOperationService operationService;
    private final AccountManagementService managementService;
    private final AccountQueryService queryService;

    public AccountService() {
        AccountRepository accountRepo = new SqliteAccountRepository();
        UserRepository userRepo = new SqliteUserRepository();
        DomainEventPublisher eventPublisher = new AuditLogRepository();

        CreateAccountUseCase createAccountUseCase = new CreateAccountUseCase(accountRepo, userRepo, eventPublisher);
        DepositWithdrawUseCase depositWithdrawUseCase = new DepositWithdrawUseCase(accountRepo, eventPublisher);
        BlockAccountUseCase blockAccountUseCase = new BlockAccountUseCase(accountRepo, eventPublisher);
        RetrieveAccountUseCase retrieveAccountUseCase = new RetrieveAccountUseCase(accountRepo);

        this.creationService = new AccountCreationService(createAccountUseCase);
        this.operationService = new AccountOperationService(depositWithdrawUseCase);
        this.managementService = new AccountManagementService(blockAccountUseCase);
        this.queryService = new AccountQueryService(retrieveAccountUseCase);
    }

    /**
     * Inyección de dependencias para testing
     * @param creationService servicio de creación de cuentas
     * @param operationService servicio de operaciones bancarias
     * @param managementService servicio de gestión de cuentas
     * @param queryService servicio de consultas de cuentas
     */
    public AccountService(AccountCreationService creationService,
                          AccountOperationService operationService,
                          AccountManagementService managementService,
                          AccountQueryService queryService) {
        this.creationService = creationService;
        this.operationService = operationService;
        this.managementService = managementService;
        this.queryService = queryService;
    }

    /**
     * Abre una nueva cuenta bancaria
     * @param ownerIdentification identificación del propietario
     * @param accountType tipo de cuenta (SAVINGS, CHECKING)
     * @param currency moneda (USD, EUR, etc.)
     * @return BankAccount la cuenta creada
     */
    public BankAccount openAccount(String ownerIdentification, AccountType accountType, String currency) {
        return creationService.openAccount(ownerIdentification, accountType, currency);
    }

    /**
     * Realiza un depósito a una cuenta
     * @param accountNumber número de cuenta destino
     * @param amount monto a depositar
     */
    public void deposit(String accountNumber, java.math.BigDecimal amount) {
        operationService.deposit(accountNumber, amount);
    }

    /**
     * Realiza un retiro de una cuenta
     * @param accountNumber número de cuenta origen
     * @param amount monto a retirar
     */
    public void withdraw(String accountNumber, java.math.BigDecimal amount) {
        operationService.withdraw(accountNumber, amount);
    }

    /**
     * Bloquea una cuenta
     * @param accountNumber número de cuenta a bloquear
     */
    public void blockAccount(String accountNumber) {
        managementService.blockAccount(accountNumber);
    }

    /**
     * Obtiene una cuenta por número de cuenta
     * @param accountNumber número de cuenta
     * @return Optional con la cuenta si existe
     */
    public Optional<BankAccount> getAccount(String accountNumber) {
        return queryService.getAccount(accountNumber);
    }

    /**
     * Obtiene todas las cuentas de un propietario
     * @param ownerIdentification identificación del propietario
     * @return Lista de cuentas del propietario
     */
    public List<BankAccount> getAccountsByOwner(String ownerIdentification) {
        return queryService.getAccountsByOwner(ownerIdentification);
    }

    /**
     * Obtiene todas las cuentas del sistema
     * @return Lista de todas las cuentas
     */
    public List<BankAccount> getAllAccounts() {
        return queryService.getAllAccounts();
    }

    /**
     * Carga una cuenta por número (lanza excepción si no existe)
     * @param accountNumber número de cuenta
     * @return BankAccount la cuenta solicitada
     */
    public BankAccount loadAccount(String accountNumber) {
        return queryService.loadAccount(accountNumber);
    }
}
