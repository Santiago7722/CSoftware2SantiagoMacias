package com.bank.shared;

import org.springframework.stereotype.Component;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

/**
 * SHARED UTILITY - AccountNumberGenerator
 * Generates unique account numbers with collision checking.
 */
@Component
public class AccountNumberGenerator {

    public String generate(Predicate<String> existsCheck) {
        String number;
        do {
            long randomPart = ThreadLocalRandom.current().nextLong(1_000_000_000L, 9_999_999_999L);
            number = "ACC" + randomPart;
        } while (existsCheck.test(number));
        return number;
    }
}
