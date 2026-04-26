package com.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * BANK MANAGEMENT SYSTEM
 * Architecture: Hexagonal (Ports and Adapters) + DDD
 *
 * Layers:
 *   domain/          → Pure business logic, no framework dependencies
 *   application/     → Use cases, Input/Output Ports, DTOs
 *   adapter/in/web/  → REST Controllers (Driving Adapters)
 *   adapter/out/     → JPA Persistence (Driven Adapters)
 *   config/          → Spring wiring, Security, Exception handling
 */
@SpringBootApplication
@EnableScheduling
public class BankApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankApplication.class, args);
    }
}
