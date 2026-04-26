package com.bank.domain.exception;
public class InvalidTransferStateException extends RuntimeException {
    public InvalidTransferStateException(String message) { super(message); }
}
