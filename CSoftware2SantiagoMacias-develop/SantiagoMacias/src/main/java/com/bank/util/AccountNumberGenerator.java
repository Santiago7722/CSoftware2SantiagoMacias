package com.bank.util;

import com.bank.repository.AccountRepository;

/**
 * UTILITY - Account Number Generator
 * Genera números únicos de cuenta de forma consistente
 */
public class AccountNumberGenerator {

    /**
     * Genera un número único de cuenta
     * Formato: ACC + 10 dígitos aleatorios
     * @param accountRepository repositorio para verificar unicidad
     * @return número de cuenta único
     */
    public String generate(AccountRepository accountRepository) {
        String number;
        do {
            number = "ACC" + String.format("%010d", (long)(Math.random() * 9_999_999_999L));
        } while (accountRepository.existsByAccountNumber(number));
        
        return number;
    }

    /**
     * Genera un número único de cuenta con prefijo personalizado
     * @param prefix prefijo personalizado para la cuenta
     * @param accountRepository repositorio para verificar unicidad
     * @return número de cuenta único con prefijo
     */
    public String generateWithPrefix(String prefix, AccountRepository accountRepository) {
        String number;
        do {
            number = prefix + String.format("%010d", (long)(Math.random() * 9_999_999_999L));
        } while (accountRepository.existsByAccountNumber(number));
        
        return number;
    }

    /**
     * Valida el formato de un número de cuenta
     * @param accountNumber número de cuenta a validar
     * @return true si el formato es válido, false en caso contrario
     */
    public boolean isValidFormat(String accountNumber) {
        return accountNumber != null && accountNumber.matches("^ACC\\d{10}$");
    }
}
