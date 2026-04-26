package com.bank.config;

import com.bank.application.port.output.BankAccountRepositoryPort;
import com.bank.application.port.output.UserRepositoryPort;
import com.bank.domain.model.aggregate.BankAccount;
import com.bank.domain.model.entity.User;
import com.bank.domain.model.valueobject.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private final UserRepositoryPort userRepository;
    private final BankAccountRepositoryPort accountRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepositoryPort userRepository, BankAccountRepositoryPort accountRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.existsByIdentificationNumber("ANA001")) { log.info("[SEED] Already seeded."); return; }
        log.info("[SEED] Seeding initial data...");
        saveUser("Sarah Johnson",   "ANA001",  "sarah.johnson@bank.com",       "5551234567", LocalDate.of(1985,3,15),  "123 Bank Street",             UserRole.INTERNAL_ANALYST,    "analyst123",    null);
        saveUser("Mike Thompson",   "TEL001",  "mike.thompson@bank.com",       "5559876543", LocalDate.of(1990,7,22),  "123 Bank Street",             UserRole.TELLER,              "teller123",     null);
        saveUser("Linda Chen",      "COM001",  "linda.chen@bank.com",          "5554567890", LocalDate.of(1988,11,5),  "123 Bank Street",             UserRole.COMMERCIAL_EMPLOYEE, "commercial123", null);
        saveUser("John Smith",      "CLI001",  "john.smith@email.com",         "5552345678", LocalDate.of(1992,5,10),  "456 Main Street Apt 3B",      UserRole.CLIENT_INDIVIDUAL,   "client123",     null);
        saveUser("Maria Garcia",    "CLI002",  "maria.garcia@email.com",       "5556789012", LocalDate.of(1987,9,28),  "789 Oak Avenue Suite 5",      UserRole.CLIENT_INDIVIDUAL,   "client456",     null);
        saveUser("Robert Williams", "CORP001", "robert.williams@techcorp.com", "5551122334", LocalDate.of(1975,1,20),  "100 Corporate Blvd Floor 10", UserRole.CLIENT_COMPANY,      "company123",    "CORP001");
        saveUser("Patricia Davis",  "SUP001",  "patricia.davis@techcorp.com",  "5553344556", LocalDate.of(1980,6,12),  "100 Corporate Blvd Floor 10", UserRole.COMPANY_SUPERVISOR,  "supervisor123", "CORP001");
        saveUser("James Wilson",    "EMP001",  "james.wilson@techcorp.com",    "5554455667", LocalDate.of(1995,3,8),   "100 Corporate Blvd Floor 10", UserRole.COMPANY_EMPLOYEE,    "employee123",   "CORP001");
        saveAccount("ACC0000000001", AccountType.SAVINGS,  "CLI001",  "15000.00",  "USD");
        saveAccount("ACC0000000002", AccountType.CHECKING, "CLI002",  "8500.00",   "USD");
        saveAccount("ACC0000000003", AccountType.BUSINESS, "CORP001", "250000.00", "USD");
        log.info("[SEED] ================================================");
        log.info("[SEED] TEST CREDENTIALS (POST /api/auth/login):");
        log.info("[SEED]   Analyst:      ID=ANA001  | Pass=analyst123");
        log.info("[SEED]   Teller:       ID=TEL001  | Pass=teller123");
        log.info("[SEED]   Commercial:   ID=COM001  | Pass=commercial123");
        log.info("[SEED]   Client 1:     ID=CLI001  | Pass=client123   | Acc=ACC0000000001 $15,000");
        log.info("[SEED]   Client 2:     ID=CLI002  | Pass=client456   | Acc=ACC0000000002 $8,500");
        log.info("[SEED]   Corp Admin:   ID=CORP001 | Pass=company123  | Acc=ACC0000000003 $250,000");
        log.info("[SEED]   Supervisor:   ID=SUP001  | Pass=supervisor123");
        log.info("[SEED]   Co. Employee: ID=EMP001  | Pass=employee123");
        log.info("[SEED] Swagger UI -> http://localhost:8080/swagger-ui.html");
        log.info("[SEED] H2 Console -> http://localhost:8080/h2-console  (jdbc:h2:mem:bankdb)");
    }

    private void saveUser(String name, String id, String email, String phone, LocalDate birth,
                          String address, UserRole role, String password, String companyId) {
        User user = User.create(name, id, email, phone, birth, address, role);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setCompanyId(companyId);
        userRepository.save(user);
    }

    private void saveAccount(String number, AccountType type, String ownerId, String balance, String currency) {
        BankAccount account = BankAccount.open(number, type, ownerId, currency);
        account.setBalance(new Money(new BigDecimal(balance), currency));
        accountRepository.save(account);
    }
}
