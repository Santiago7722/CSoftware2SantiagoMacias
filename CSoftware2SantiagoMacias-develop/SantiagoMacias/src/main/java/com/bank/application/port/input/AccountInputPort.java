package com.bank.application.port.input;

import com.bank.application.dto.BankingDto.*;
import java.util.List;

/**
 * APPLICATION INPUT PORT - AccountInputPort
 * Defines account operations exposed to the interface layer.
 */
public interface AccountInputPort {
    AccountResponse openAccount(OpenAccountCommand command);
    AccountResponse getAccount(String accountNumber);
    List<AccountResponse> getAccountsByOwner(String ownerIdentificationNumber);
    List<AccountResponse> getAllAccounts();
    AccountResponse deposit(String accountNumber, DepositWithdrawCommand command);
    AccountResponse withdraw(String accountNumber, DepositWithdrawCommand command);
    AccountResponse blockAccount(String accountNumber);
    AccountResponse unblockAccount(String accountNumber);
}
