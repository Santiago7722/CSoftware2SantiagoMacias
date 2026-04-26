package com.bank.repository;

import com.bank.model.BankAccount;

import java.util.List;
import java.util.Optional;

/**
 * PUERTO — Interfaz del repositorio de cuentas.
 * El dominio define qué necesita. La infraestructura decide cómo.
 */
public interface AccountRepository {
    void save(BankAccount account);
    Optional<BankAccount> findByAccountNumber(String accountNumber);
    List<BankAccount> findByOwnerId(String ownerId);
    List<BankAccount> findAll();
    boolean existsByAccountNumber(String accountNumber);
}
