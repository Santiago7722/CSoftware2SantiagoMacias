package com.bank.util;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.bank.model.AccountStatus;
import com.bank.model.AccountType;
import com.bank.model.BankAccount;
import com.bank.model.Money;
import com.bank.model.User;
import com.bank.model.UserRole;
import com.bank.model.UserStatus;
import com.bank.repository.AccountRepository;
import com.bank.repository.SqliteAccountRepository;
import com.bank.repository.SqliteUserRepository;
import com.bank.repository.UserRepository;
import com.bank.service.AuthService;

public class DataSeeder {
    private final UserRepository userRepo;
    private final AccountRepository accountRepo;

    public DataSeeder() {
        this.userRepo = new SqliteUserRepository();
        this.accountRepo = new SqliteAccountRepository();
    }

    public void seedIfEmpty() {
        if (!userRepo.findAll().isEmpty()) {
            System.out.println("[SEED] Database already seeded.");
            return;
        }
        System.out.println("[SEED] Seeding initial data...");

        // ---- INTERNAL ANALYST ----
        User analyst = new User();
        analyst.setFullName("Sarah Johnson");
        analyst.setIdentificationNumber("ANA001");
        analyst.setEmail("sarah.johnson@bank.com");
        analyst.setPhone("5551234567");
        analyst.setBirthDate(LocalDate.of(1985, 3, 15));
        analyst.setAddress("123 Bank Street, Financial District");
        analyst.setRole(UserRole.INTERNAL_ANALYST);
        analyst.setStatus(UserStatus.ACTIVE);
        analyst.setPasswordHash(AuthService.hashPassword("analyst123"));
        userRepo.save(analyst);

        // ---- TELLER ----
        User teller = new User();
        teller.setFullName("Mike Thompson");
        teller.setIdentificationNumber("TEL001");
        teller.setEmail("mike.thompson@bank.com");
        teller.setPhone("5559876543");
        teller.setBirthDate(LocalDate.of(1990, 7, 22));
        teller.setAddress("123 Bank Street, Financial District");
        teller.setRole(UserRole.TELLER);
        teller.setStatus(UserStatus.ACTIVE);
        teller.setPasswordHash(AuthService.hashPassword("teller123"));
        userRepo.save(teller);

        // ---- COMMERCIAL EMPLOYEE ----
        User commercial = new User();
        commercial.setFullName("Linda Chen");
        commercial.setIdentificationNumber("COM001");
        commercial.setEmail("linda.chen@bank.com");
        commercial.setPhone("5554567890");
        commercial.setBirthDate(LocalDate.of(1988, 11, 5));
        commercial.setAddress("123 Bank Street, Financial District");
        commercial.setRole(UserRole.COMMERCIAL_EMPLOYEE);
        commercial.setStatus(UserStatus.ACTIVE);
        commercial.setPasswordHash(AuthService.hashPassword("commercial123"));
        userRepo.save(commercial);

        // ---- CLIENT INDIVIDUAL 1 ----
        User client1 = new User();
        client1.setFullName("John Smith");
        client1.setIdentificationNumber("CLI001");
        client1.setEmail("john.smith@email.com");
        client1.setPhone("5552345678");
        client1.setBirthDate(LocalDate.of(1992, 5, 10));
        client1.setAddress("456 Main Street, Apt 3B");
        client1.setRole(UserRole.CLIENT_INDIVIDUAL);
        client1.setStatus(UserStatus.ACTIVE);
        client1.setPasswordHash(AuthService.hashPassword("client123"));
        userRepo.save(client1);

        // ---- CLIENT INDIVIDUAL 2 ----
        User client2 = new User();
        client2.setFullName("Maria Garcia");
        client2.setIdentificationNumber("CLI002");
        client2.setEmail("maria.garcia@email.com");
        client2.setPhone("5556789012");
        client2.setBirthDate(LocalDate.of(1987, 9, 28));
        client2.setAddress("789 Oak Avenue, Suite 5");
        client2.setRole(UserRole.CLIENT_INDIVIDUAL);
        client2.setStatus(UserStatus.ACTIVE);
        client2.setPasswordHash(AuthService.hashPassword("client456"));
        userRepo.save(client2);

        // ---- CLIENT COMPANY ----
        User companyAdmin = new User();
        companyAdmin.setFullName("Robert Williams");
        companyAdmin.setIdentificationNumber("CORP001");
        companyAdmin.setEmail("robert.williams@techcorp.com");
        companyAdmin.setPhone("5551122334");
        companyAdmin.setBirthDate(LocalDate.of(1975, 1, 20));
        companyAdmin.setAddress("100 Corporate Blvd, Floor 10");
        companyAdmin.setRole(UserRole.CLIENT_COMPANY);
        companyAdmin.setStatus(UserStatus.ACTIVE);
        companyAdmin.setPasswordHash(AuthService.hashPassword("company123"));
        companyAdmin.setCompanyId("CORP001");
        companyAdmin.setRelatedEntityId("CORP001");
        userRepo.save(companyAdmin);

        // ---- COMPANY SUPERVISOR ----
        User supervisor = new User();
        supervisor.setFullName("Patricia Davis");
        supervisor.setIdentificationNumber("SUP001");
        supervisor.setEmail("patricia.davis@techcorp.com");
        supervisor.setPhone("5553344556");
        supervisor.setBirthDate(LocalDate.of(1980, 6, 12));
        supervisor.setAddress("100 Corporate Blvd, Floor 10");
        supervisor.setRole(UserRole.COMPANY_SUPERVISOR);
        supervisor.setStatus(UserStatus.ACTIVE);
        supervisor.setPasswordHash(AuthService.hashPassword("supervisor123"));
        supervisor.setCompanyId("CORP001");
        userRepo.save(supervisor);

        // ---- COMPANY EMPLOYEE ----
        User companyEmp = new User();
        companyEmp.setFullName("James Wilson");
        companyEmp.setIdentificationNumber("EMP001");
        companyEmp.setEmail("james.wilson@techcorp.com");
        companyEmp.setPhone("5554455667");
        companyEmp.setBirthDate(LocalDate.of(1995, 3, 8));
        companyEmp.setAddress("100 Corporate Blvd, Floor 10");
        companyEmp.setRole(UserRole.COMPANY_EMPLOYEE);
        companyEmp.setStatus(UserStatus.ACTIVE);
        companyEmp.setPasswordHash(AuthService.hashPassword("employee123"));
        companyEmp.setCompanyId("CORP001");
        userRepo.save(companyEmp);

        // ---- INITIAL ACCOUNTS ----
        // John Smith — savings
        BankAccount acc1 = new BankAccount();
        acc1.setAccountNumber("ACC0000000001");
        acc1.setAccountType(AccountType.SAVINGS);
        acc1.setOwnerId("CLI001");
        acc1.setBalance(new Money(new BigDecimal("15000.00"), "USD"));
        acc1.setStatus(AccountStatus.ACTIVE);
        acc1.setOpeningDate(LocalDate.now().minusMonths(6));
        accountRepo.save(acc1);

        // Maria Garcia — checking
        BankAccount acc2 = new BankAccount();
        acc2.setAccountNumber("ACC0000000002");
        acc2.setAccountType(AccountType.CHECKING);
        acc2.setOwnerId("CLI002");
        acc2.setBalance(new Money(new BigDecimal("8500.00"), "USD"));
        acc2.setStatus(AccountStatus.ACTIVE);
        acc2.setOpeningDate(LocalDate.now().minusMonths(12));
        accountRepo.save(acc2);

        // TechCorp — business
        BankAccount acc3 = new BankAccount();
        acc3.setAccountNumber("ACC0000000003");
        acc3.setAccountType(AccountType.BUSINESS);
        acc3.setOwnerId("CORP001");
        acc3.setBalance(new Money(new BigDecimal("250000.00"), "USD"));
        acc3.setStatus(AccountStatus.ACTIVE);
        acc3.setOpeningDate(LocalDate.now().minusYears(2));
        accountRepo.save(acc3);

        System.out.println("[SEED] Initial data seeded successfully!");
        System.out.println("[SEED] ================================");
        System.out.println("[SEED] TEST CREDENTIALS:");
        System.out.println("[SEED]   Analyst:    ID=ANA001, Pass=analyst123");
        System.out.println("[SEED]   Teller:     ID=TEL001, Pass=teller123");
        System.out.println("[SEED]   Commercial: ID=COM001, Pass=commercial123");
        System.out.println("[SEED]   Client 1:   ID=CLI001, Pass=client123  (balance: $15,000)");
        System.out.println("[SEED]   Client 2:   ID=CLI002, Pass=client456  (balance: $8,500)");
        System.out.println("[SEED]   Corp Admin: ID=CORP001, Pass=company123 (balance: $250,000)");
        System.out.println("[SEED]   Supervisor: ID=SUP001, Pass=supervisor123");
        System.out.println("[SEED]   Co. Employ: ID=EMP001, Pass=employee123");
        System.out.println("[SEED] ================================");
    }
}