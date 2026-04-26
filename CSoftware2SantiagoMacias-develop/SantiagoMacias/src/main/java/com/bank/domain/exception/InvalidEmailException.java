package com.bank.domain.exception;
public class InvalidEmailException extends DomainValidationException {
    public InvalidEmailException(String message) { super(message); }
}
