package com.bank.domain.exception;
public class InvalidLoanStateTransitionException extends RuntimeException {
    public InvalidLoanStateTransitionException(String message) { super(message); }
}
