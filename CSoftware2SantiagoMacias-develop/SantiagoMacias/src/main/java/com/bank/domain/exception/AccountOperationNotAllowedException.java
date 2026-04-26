package com.bank.domain.exception;
public class AccountOperationNotAllowedException extends RuntimeException {
    public AccountOperationNotAllowedException(String message) { super(message); }
}
