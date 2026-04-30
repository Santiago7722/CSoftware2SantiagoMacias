package com.bank.config;

import com.bank.domain.service.LoanDisbursementDomainService;
import com.bank.domain.service.TransferDomainService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * CONFIG - AppConfig
 *
 * Wires domain services as Spring beans.
 * Domain services are pure Java — they just need their parameters injected here.
 */
@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
public class AppConfig {

    @Value("${app.transfer.approval-threshold}")
    private BigDecimal transferApprovalThreshold;

    @Bean
    public TransferDomainService transferDomainService() {
        return new TransferDomainService(transferApprovalThreshold);
    }

    @Bean
    public LoanDisbursementDomainService loanDisbursementDomainService() {
        return new LoanDisbursementDomainService();
    }
}
