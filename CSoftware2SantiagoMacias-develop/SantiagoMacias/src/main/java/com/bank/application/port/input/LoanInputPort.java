package com.bank.application.port.input;

import com.bank.application.dto.BankingDto.*;
import java.util.List;

/**
 * APPLICATION INPUT PORT - LoanInputPort
 * Defines loan lifecycle operations.
 */
public interface LoanInputPort {
    LoanResponse requestLoan(RequestLoanCommand command);
    LoanResponse approveLoan(Long loanId, ApproveLoanCommand command);
    LoanResponse rejectLoan(Long loanId, RejectLoanCommand command);
    LoanResponse disburseLoan(Long loanId);
    LoanResponse getLoanById(Long loanId);
    List<LoanResponse> getLoansByClient(String clientIdentificationNumber);
    List<LoanResponse> getAllLoans();
    List<LoanResponse> getLoansByStatus(String status);
}
