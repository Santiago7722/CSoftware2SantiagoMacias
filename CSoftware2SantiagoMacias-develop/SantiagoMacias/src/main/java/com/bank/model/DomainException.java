package com.bank.model;

/**
 * Excepción base para violaciones de reglas del dominio bancario.
 */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
