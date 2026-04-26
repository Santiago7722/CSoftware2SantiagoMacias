package com.bank.util;

import com.bank.model.*;
import com.bank.service.*;

import java.math.BigDecimal;
import java.util.List;

public class ConsoleUI {
    private final AuthService authService;
    private final UserService userService;
    private final AccountService accountService;
    private final LoanService loanService;
    private final TransferService transferService;
    private final AuditService auditService;

    public ConsoleUI() {
        this.authService = new AuthService();
        this.userService = new UserService();
        this.accountService = new AccountService();
        this.loanService = new LoanService();
        this.transferService = new TransferService();
        this.auditService = new AuditService();
    }

    public void start() {
        printBanner();
        while (true) {
            try {
                if (!AuthService.isLoggedIn()) {
                    showLoginMenu();
                } else {
                    showMainMenu();
                }
            } catch (SecurityException | DomainException e) {
                System.out.println("\n  [ACCESS DENIED / ERROR] " + e.getMessage());
                InputHelper.pause();
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("\n  [ERROR] " + e.getMessage());
                InputHelper.pause();
            } catch (Exception e) {
                System.out.println("\n  [UNEXPECTED ERROR] " + e.getMessage());
                InputHelper.pause();
            }
        }
    }

    private void printBanner() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("        BANK MANAGEMENT INFORMATION SYSTEM");
        System.out.println("=".repeat(60));
    }

    // ======================== LOGIN ========================

    private void showLoginMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("  1. Login");
        System.out.println("  2. Register New User");
        System.out.println("  0. Exit");
        int choice = InputHelper.readChoice("  Select option: ", 0, 2);
        switch (choice) {
            case 1 -> doLogin();
            case 2 -> doRegisterUser();
            case 0 -> { System.out.println("\nGoodbye!"); System.exit(0); }
        }
    }

    private void doLogin() {
        System.out.println("\n--- LOGIN ---");
        String id = InputHelper.readRequiredString("  Identification Number: ");
        String pass = InputHelper.readRequiredString("  Password: ");
        authService.login(id, pass);
    }

    private void doRegisterUser() {
        System.out.println("\n--- REGISTER NEW USER ---");
        System.out.println("  1. Individual Client");
        System.out.println("  2. Company Client (Legal Representative)");
        System.out.println("  3. Company Employee");
        System.out.println("  4. Company Supervisor");
        System.out.println("  0. Cancel");
        int choice = InputHelper.readChoice("  Select: ", 0, 4);
        if (choice == 0) return;

        UserRole[] roles = {UserRole.CLIENT_INDIVIDUAL, UserRole.CLIENT_COMPANY, UserRole.COMPANY_EMPLOYEE, UserRole.COMPANY_SUPERVISOR};
        UserRole selectedRole = roles[choice - 1];

        User user = new User();
        user.setFullName(InputHelper.readRequiredString("  Full Name: "));
        user.setIdentificationNumber(InputHelper.readRequiredString("  Identification Number (DNI/NIT): "));
        user.setEmail(InputHelper.readRequiredString("  Email: "));
        user.setPhone(InputHelper.readRequiredString("  Phone (7-15 digits): "));
        user.setAddress(InputHelper.readRequiredString("  Address: "));
        user.setRole(selectedRole);
        user.setStatus(UserStatus.ACTIVE);

        if (selectedRole == UserRole.CLIENT_INDIVIDUAL) {
            user.setBirthDate(InputHelper.readDate("  Birth Date"));
        }
        if (selectedRole == UserRole.COMPANY_EMPLOYEE || selectedRole == UserRole.COMPANY_SUPERVISOR
                || selectedRole == UserRole.CLIENT_COMPANY) {
            user.setCompanyId(InputHelper.readRequiredString("  Company ID (NIT): "));
        }

        String password = InputHelper.readRequiredString("  Password: ");
        User saved = userService.createUser(user, password);
        System.out.println("\n  [SUCCESS] User registered! ID: " + saved.getUserId() + " | Name: " + saved.getFullName());
        InputHelper.pause();
    }

    // ======================== MAIN MENU ========================

    private void showMainMenu() {
        User current = AuthService.getCurrentUser();
        System.out.println("\n--- MAIN MENU | " + current.getFullName() + " (" + current.getRole().getDisplayName() + ") ---");
        switch (current.getRole()) {
            case INTERNAL_ANALYST -> showAnalystMenu();
            case TELLER -> showTellerMenu();
            case COMMERCIAL_EMPLOYEE -> showCommercialMenu();
            case CLIENT_INDIVIDUAL -> showIndividualClientMenu();
            case CLIENT_COMPANY -> showCompanyClientMenu();
            case COMPANY_EMPLOYEE -> showCompanyEmployeeMenu();
            case COMPANY_SUPERVISOR -> showCompanySupervisorMenu();
        }
    }

    // ======================== ANALYST MENU ========================

    private void showAnalystMenu() {
        System.out.println("  1.  Manage Loans");
        System.out.println("  2.  View All Clients");
        System.out.println("  3.  View All Accounts");
        System.out.println("  4.  View All Transfers");
        System.out.println("  5.  Deposit to Account");
        System.out.println("  6.  Withdraw from Account");
        System.out.println("  7.  Block Account");
        System.out.println("  8.  View Audit Log");
        System.out.println("  9.  Register Bank Employee");
        System.out.println("  0.  Logout");
        int choice = InputHelper.readChoice("  Select: ", 0, 9);
        switch (choice) {
            case 1 -> showLoanManagementMenu();
            case 2 -> listAllUsers();
            case 3 -> listAllAccounts();
            case 4 -> listAllTransfers();
            case 5 -> doDeposit();
            case 6 -> doWithdraw();
            case 7 -> doBlockAccount();
            case 8 -> showAuditLog();
            case 9 -> doRegisterBankEmployee();
            case 0 -> authService.logout();
        }
    }

    private void showLoanManagementMenu() {
        System.out.println("\n--- LOAN MANAGEMENT ---");
        System.out.println("  1. View All Loans Under Review");
        System.out.println("  2. Approve Loan");
        System.out.println("  3. Reject Loan");
        System.out.println("  4. Disburse Loan");
        System.out.println("  5. View All Loans");
        System.out.println("  0. Back");
        int choice = InputHelper.readChoice("  Select: ", 0, 5);
        switch (choice) {
            case 1 -> listLoansByStatus(LoanStatus.UNDER_REVIEW);
            case 2 -> doApproveLoan();
            case 3 -> doRejectLoan();
            case 4 -> doDisburseLoan();
            case 5 -> listAllLoans();
            case 0 -> {}
        }
    }

    private void doApproveLoan() {
        listLoansByStatus(LoanStatus.UNDER_REVIEW);
        int loanId = InputHelper.readInt("\n  Enter Loan ID to approve: ");
        BigDecimal amount = InputHelper.readDecimal("  Approved Amount: ");
        BigDecimal rate = InputHelper.readDecimal("  Annual Interest Rate (%): ");
        int term = InputHelper.readInt("  Term (months): ");
        Loan loan = loanService.approveLoan(loanId, amount, rate, term);
        System.out.println("  [OK] Loan approved: " + loan);
        InputHelper.pause();
    }

    private void doRejectLoan() {
        listLoansByStatus(LoanStatus.UNDER_REVIEW);
        int loanId = InputHelper.readInt("\n  Enter Loan ID to reject: ");
        String reason = InputHelper.readRequiredString("  Rejection reason: ");
        loanService.rejectLoan(loanId, reason);
        InputHelper.pause();
    }

    private void doDisburseLoan() {
        listLoansByStatus(LoanStatus.APPROVED);
        int loanId = InputHelper.readInt("\n  Enter Loan ID to disburse: ");
        loanService.disburseLoan(loanId);
        InputHelper.pause();
    }

    private void doRegisterBankEmployee() {
        System.out.println("\n--- REGISTER BANK EMPLOYEE ---");
        System.out.println("  1. Teller");
        System.out.println("  2. Commercial Employee");
        System.out.println("  3. Internal Analyst");
        int roleChoice = InputHelper.readChoice("  Role: ", 1, 3);
        UserRole role = switch (roleChoice) {
            case 1 -> UserRole.TELLER;
            case 2 -> UserRole.COMMERCIAL_EMPLOYEE;
            default -> UserRole.INTERNAL_ANALYST;
        };
        User user = new User();
        user.setFullName(InputHelper.readRequiredString("  Full Name: "));
        user.setIdentificationNumber(InputHelper.readRequiredString("  Identification Number: "));
        user.setEmail(InputHelper.readRequiredString("  Email: "));
        user.setPhone(InputHelper.readRequiredString("  Phone: "));
        user.setAddress(InputHelper.readRequiredString("  Address: "));
        user.setBirthDate(InputHelper.readDate("  Birth Date"));
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        String password = InputHelper.readRequiredString("  Password: ");
        User saved = userService.createUser(user, password);
        System.out.println("  [OK] Employee registered. ID: " + saved.getUserId());
        InputHelper.pause();
    }

    private void showAuditLog() {
        System.out.println("\n--- AUDIT LOG ---");
        System.out.println("  1. View All Logs");
        System.out.println("  2. View Logs by Product ID");
        System.out.println("  3. View Logs by User ID");
        System.out.println("  0. Back");
        int choice = InputHelper.readChoice("  Select: ", 0, 3);
        switch (choice) {
            case 1 -> printAuditLogs(auditService.getAllLogs());
            case 2 -> { String pid = InputHelper.readRequiredString("  Product ID: "); printAuditLogs(auditService.getLogsByProduct(pid)); }
            case 3 -> { int uid = InputHelper.readInt("  User ID: "); printAuditLogs(auditService.getLogsByUser(uid)); }
            case 0 -> {}
        }
        InputHelper.pause();
    }

    private void printAuditLogs(List<AuditLog> logs) {
        if (logs.isEmpty()) { System.out.println("  No records found."); return; }
        System.out.printf("%n  %-36s %-30s %-10s %-20s%n", "LOG ID", "OPERATION", "USER ID", "DATETIME");
        System.out.println("  " + "-".repeat(100));
        for (AuditLog log : logs) {
            System.out.printf("  %-36s %-30s %-10d %-20s%n",
                    log.getLogId() != null ? log.getLogId().substring(0, Math.min(36, log.getLogId().length())) : "-",
                    log.getOperationType(), log.getUserId(), log.getOperationDateTime());
            System.out.println("    Details: " + log.getDetailData());
        }
    }

    // ======================== TELLER MENU ========================

    private void showTellerMenu() {
        System.out.println("  1. Check Account Balance");
        System.out.println("  2. Deposit");
        System.out.println("  3. Withdraw");
        System.out.println("  4. Open New Account for Client");
        System.out.println("  0. Logout");
        int choice = InputHelper.readChoice("  Select: ", 0, 4);
        switch (choice) {
            case 1 -> doCheckBalance();
            case 2 -> doDeposit();
            case 3 -> doWithdraw();
            case 4 -> doOpenAccount();
            case 0 -> authService.logout();
        }
    }

    // ======================== COMMERCIAL MENU ========================

    private void showCommercialMenu() {
        System.out.println("  1. View Client Information");
        System.out.println("  2. Open Account for Client");
        System.out.println("  3. Request Loan for Client");
        System.out.println("  4. View Loans Under Review");
        System.out.println("  5. View All Loans");
        System.out.println("  0. Logout");
        int choice = InputHelper.readChoice("  Select: ", 0, 5);
        switch (choice) {
            case 1 -> doViewClientInfo();
            case 2 -> doOpenAccount();
            case 3 -> doRequestLoan();
            case 4 -> listLoansByStatus(LoanStatus.UNDER_REVIEW);
            case 5 -> listAllLoans();
            case 0 -> authService.logout();
        }
    }

    // ======================== INDIVIDUAL CLIENT MENU ========================

    private void showIndividualClientMenu() {
        User current = AuthService.getCurrentUser();
        System.out.println("  1. View My Accounts");
        System.out.println("  2. Open New Account");
        System.out.println("  3. View My Loans");
        System.out.println("  4. Request a Loan");
        System.out.println("  5. Create Transfer");
        System.out.println("  6. View My Transfers");
        System.out.println("  0. Logout");
        int choice = InputHelper.readChoice("  Select: ", 0, 6);
        switch (choice) {
            case 1 -> listMyAccounts(current.getIdentificationNumber());
            case 2 -> doOpenAccount();
            case 3 -> listMyLoans(current.getIdentificationNumber());
            case 4 -> doRequestLoan();
            case 5 -> doCreateTransfer();
            case 6 -> doViewMyTransfers();
            case 0 -> authService.logout();
        }
    }

    // ======================== COMPANY CLIENT MENU ========================

    private void showCompanyClientMenu() {
        User current = AuthService.getCurrentUser();
        System.out.println("  1. View Company Accounts");
        System.out.println("  2. Open New Account");
        System.out.println("  3. View Company Loans");
        System.out.println("  4. Request a Loan");
        System.out.println("  5. View Pending Transfers (Approve/Reject)");
        System.out.println("  0. Logout");
        int choice = InputHelper.readChoice("  Select: ", 0, 5);
        switch (choice) {
            case 1 -> listMyAccounts(current.getIdentificationNumber());
            case 2 -> doOpenAccount();
            case 3 -> listMyLoans(current.getIdentificationNumber());
            case 4 -> doRequestLoan();
            case 5 -> showPendingTransferApprovalMenu();
            case 0 -> authService.logout();
        }
    }

    // ======================== COMPANY EMPLOYEE MENU ========================

    private void showCompanyEmployeeMenu() {
        User current = AuthService.getCurrentUser();
        System.out.println("  1. View Company Accounts");
        System.out.println("  2. Create Transfer");
        System.out.println("  3. View Company Transfers");
        System.out.println("  0. Logout");
        int choice = InputHelper.readChoice("  Select: ", 0, 3);
        switch (choice) {
            case 1 -> listMyAccounts(current.getCompanyId());
            case 2 -> doCreateTransfer();
            case 3 -> doViewCompanyTransfers();
            case 0 -> authService.logout();
        }
    }

    // ======================== COMPANY SUPERVISOR MENU ========================

    private void showCompanySupervisorMenu() {
        User current = AuthService.getCurrentUser();
        System.out.println("  1. View Company Accounts");
        System.out.println("  2. View Pending Transfers (Approve/Reject)");
        System.out.println("  3. View All Company Transfers");
        System.out.println("  0. Logout");
        int choice = InputHelper.readChoice("  Select: ", 0, 3);
        switch (choice) {
            case 1 -> listMyAccounts(current.getCompanyId());
            case 2 -> showPendingTransferApprovalMenu();
            case 3 -> doViewCompanyTransfers();
            case 0 -> authService.logout();
        }
    }

    // ======================== SHARED OPERATIONS ========================

    private void doCheckBalance() {
        String accNum = InputHelper.readRequiredString("\n  Account Number: ");
        var accOpt = accountService.getAccount(accNum);
        if (accOpt.isPresent()) {
            BankAccount acc = accOpt.get();
            System.out.printf("%n  Account: %s | Type: %s | Balance: %.2f %s | Status: %s%n",
                    acc.getAccountNumber(), acc.getAccountType(),
                    acc.getCurrentBalance(), acc.getCurrency(), acc.getStatus());
        } else {
            System.out.println("  Account not found.");
        }
        InputHelper.pause();
    }

    private void doDeposit() {
        System.out.println("\n--- DEPOSIT ---");
        String accNum = InputHelper.readRequiredString("  Account Number: ");
        BigDecimal amount = InputHelper.readDecimal("  Amount: ");
        accountService.deposit(accNum, amount);
        InputHelper.pause();
    }

    private void doWithdraw() {
        System.out.println("\n--- WITHDRAWAL ---");
        String accNum = InputHelper.readRequiredString("  Account Number: ");
        BigDecimal amount = InputHelper.readDecimal("  Amount: ");
        accountService.withdraw(accNum, amount);
        InputHelper.pause();
    }

    private void doOpenAccount() {
        System.out.println("\n--- OPEN ACCOUNT ---");
        User current = AuthService.getCurrentUser();
        String ownerId;
        if (current.hasRole(UserRole.CLIENT_INDIVIDUAL, UserRole.CLIENT_COMPANY)) {
            ownerId = current.getIdentificationNumber();
        } else {
            ownerId = InputHelper.readRequiredString("  Client Identification Number: ");
        }
        System.out.println("  Account Type: 1=SAVINGS  2=CHECKING  3=INVESTMENT  4=PERSONAL  5=BUSINESS");
        int typeChoice = InputHelper.readChoice("  Select: ", 1, 5);
        AccountType type = AccountType.values()[typeChoice - 1];
        String currency = InputHelper.readRequiredString("  Currency (e.g. USD, EUR): ");
        BankAccount acc = accountService.openAccount(ownerId, type, currency);
        System.out.println("\n  [OK] Account opened: " + acc.getAccountNumber() + " | Type: " + acc.getAccountType());
        InputHelper.pause();
    }

    private void doBlockAccount() {
        String accNum = InputHelper.readRequiredString("\n  Account Number to block: ");
        accountService.blockAccount(accNum);
        System.out.println("  [OK] Account blocked.");
        InputHelper.pause();
    }

    private void doRequestLoan() {
        System.out.println("\n--- REQUEST LOAN ---");
        User current = AuthService.getCurrentUser();
        String clientId = current.hasRole(UserRole.CLIENT_INDIVIDUAL, UserRole.CLIENT_COMPANY)
                ? current.getIdentificationNumber()
                : InputHelper.readRequiredString("  Client Identification Number: ");
        String type = InputHelper.readRequiredString("  Loan Type (e.g. Personal, Mortgage, Business): ");
        BigDecimal amount = InputHelper.readDecimal("  Requested Amount: ");
        int term = InputHelper.readInt("  Term (months): ");
        String disbAccount = InputHelper.readRequiredString("  Disbursement Account Number: ");
        Loan loan = loanService.requestLoan(clientId, type, amount, term, disbAccount);
        System.out.println("  [OK] Loan submitted. ID: " + loan.getLoanId() + " | Status: " + loan.getStatus());
        InputHelper.pause();
    }

    private void doCreateTransfer() {
        System.out.println("\n--- CREATE TRANSFER ---");
        System.out.printf("  Note: Company employee transfers above $%.2f require supervisor approval.%n",
                transferService.getApprovalThreshold());
        String src = InputHelper.readRequiredString("  Source Account Number: ");
        String dst = InputHelper.readRequiredString("  Destination Account Number: ");
        BigDecimal amount = InputHelper.readDecimal("  Amount: ");
        Transfer t = transferService.createTransfer(src, dst, amount);
        System.out.println("  [OK] Transfer " + t.getTransferId() + " | Status: " + t.getStatus());
        InputHelper.pause();
    }

    private void doViewMyTransfers() {
        System.out.println("\n--- MY TRANSFERS ---");
        String accNum = InputHelper.readRequiredString("  Account Number: ");
        printTransfers(transferService.getTransfersForAccount(accNum));
        InputHelper.pause();
    }

    private void doViewCompanyTransfers() {
        System.out.println("\n--- COMPANY TRANSFERS ---");
        String accNum = InputHelper.readRequiredString("  Company Account Number: ");
        printTransfers(transferService.getTransfersForAccount(accNum));
        InputHelper.pause();
    }

    private void showPendingTransferApprovalMenu() {
        System.out.println("\n--- PENDING TRANSFERS ---");
        List<Transfer> pending = transferService.getPendingTransfers();
        printTransfers(pending);
        if (pending.isEmpty()) { InputHelper.pause(); return; }
        System.out.println("  1. Approve a Transfer");
        System.out.println("  2. Reject a Transfer");
        System.out.println("  0. Back");
        int choice = InputHelper.readChoice("  Select: ", 0, 2);
        if (choice == 0) return;
        int transferId = InputHelper.readInt("  Transfer ID: ");
        if (choice == 1) {
            transferService.approveTransfer(transferId);
            System.out.println("  [OK] Transfer approved and executed.");
        } else {
            String reason = InputHelper.readRequiredString("  Rejection reason: ");
            transferService.rejectTransfer(transferId, reason);
            System.out.println("  [OK] Transfer rejected.");
        }
        InputHelper.pause();
    }

    private void doViewClientInfo() {
        String clientId = InputHelper.readRequiredString("\n  Client Identification Number: ");
        var clientOpt = userService.findByIdentification(clientId);
        if (clientOpt.isEmpty()) { System.out.println("  Client not found."); InputHelper.pause(); return; }
        User client = clientOpt.get();
        System.out.println("\n  --- Client Info ---");
        System.out.println("  Name:   " + client.getFullName());
        System.out.println("  ID:     " + client.getIdentificationNumber());
        System.out.println("  Email:  " + client.getEmail());
        System.out.println("  Role:   " + client.getRole());
        System.out.println("  Status: " + client.getStatus());
        printAccounts(accountService.getAccountsByOwner(clientId));
        InputHelper.pause();
    }

    // ======================== LIST HELPERS ========================

    private void listAllUsers() {
        List<User> users = userService.getAllUsers();
        System.out.println("\n--- ALL USERS (" + users.size() + ") ---");
        System.out.printf("  %-5s %-25s %-15s %-25s %-15s %-8s%n", "ID", "NAME", "ID NUMBER", "EMAIL", "ROLE", "STATUS");
        System.out.println("  " + "-".repeat(100));
        for (User u : users) {
            System.out.printf("  %-5d %-25s %-15s %-25s %-15s %-8s%n",
                    u.getUserId(), truncate(u.getFullName(), 24), u.getIdentificationNumber(),
                    truncate(u.getEmail(), 24), u.getRole().name(), u.getStatus());
        }
        InputHelper.pause();
    }

    private void listAllAccounts() {
        printAccounts(accountService.getAllAccounts());
        InputHelper.pause();
    }

    private void listMyAccounts(String ownerId) {
        System.out.println("\n--- ACCOUNTS FOR " + ownerId + " ---");
        printAccounts(accountService.getAccountsByOwner(ownerId));
        InputHelper.pause();
    }

    private void printAccounts(List<BankAccount> accounts) {
        if (accounts.isEmpty()) { System.out.println("  No accounts found."); return; }
        System.out.printf("  %-15s %-12s %-12s %-15s %-6s %-8s%n", "ACCOUNT #", "TYPE", "OWNER", "BALANCE", "CURR", "STATUS");
        System.out.println("  " + "-".repeat(75));
        for (BankAccount a : accounts) {
            System.out.printf("  %-15s %-12s %-12s %-15.2f %-6s %-8s%n",
                    a.getAccountNumber(), a.getAccountType(), a.getOwnerId(),
                    a.getCurrentBalance(), a.getCurrency(), a.getStatus());
        }
    }

    private void listAllLoans() {
        printLoans(loanService.getAllLoans());
        InputHelper.pause();
    }

    private void listLoansByStatus(LoanStatus status) {
        System.out.println("\n--- LOANS [" + status + "] ---");
        printLoans(loanService.getLoansByStatus(status));
        InputHelper.pause();
    }

    private void listMyLoans(String clientId) {
        System.out.println("\n--- MY LOANS ---");
        printLoans(loanService.getLoansByClient(clientId));
        InputHelper.pause();
    }

    private void printLoans(List<Loan> loans) {
        if (loans.isEmpty()) { System.out.println("  No loans found."); return; }
        System.out.printf("  %-6s %-12s %-12s %-12s %-12s %-5s %-15s%n", "ID", "TYPE", "CLIENT", "REQUESTED", "APPROVED", "TERM", "STATUS");
        System.out.println("  " + "-".repeat(80));
        for (Loan l : loans) {
            System.out.printf("  %-6d %-12s %-12s %-12.2f %-12s %-5d %-15s%n",
                    l.getLoanId(), truncate(l.getLoanType(), 11), l.getClientId(),
                    l.getRequestedAmount().getAmount(),
                    l.getApprovedAmount() != null ? String.format("%.2f", l.getApprovedAmount().getAmount()) : "N/A",
                    l.getTermMonths(), l.getStatus());
        }
    }

    private void listAllTransfers() {
        System.out.println("\n--- ALL TRANSFERS ---");
        printTransfers(transferService.getAllTransfers());
        InputHelper.pause();
    }

    private void printTransfers(List<Transfer> transfers) {
        if (transfers.isEmpty()) { System.out.println("  No transfers found."); return; }
        System.out.printf("  %-6s %-15s %-15s %-12s %-20s %-15s%n", "ID", "FROM", "TO", "AMOUNT", "CREATED", "STATUS");
        System.out.println("  " + "-".repeat(90));
        for (Transfer t : transfers) {
            System.out.printf("  %-6d %-15s %-15s %-12.2f %-20s %-15s%n",
                    t.getTransferId(), t.getSourceAccount(), t.getDestinationAccount(),
                    t.getAmount().getAmount(),
                    t.getCreationDate().toString().substring(0, 19), t.getStatus());
        }
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() > maxLen ? s.substring(0, maxLen - 1) + "…" : s;
    }
}
