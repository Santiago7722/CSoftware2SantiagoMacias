package com.bank.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.math.BigDecimal;

/**
 * CONFIG - ApplicationProperties
 * Maps custom application properties from application.properties
 */
@ConfigurationProperties(prefix = "app")
@EnableConfigurationProperties(ApplicationProperties.class)
public class ApplicationProperties {

    private Jwt jwt = new Jwt();
    private Transfer transfer = new Transfer();

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public Transfer getTransfer() {
        return transfer;
    }

    public void setTransfer(Transfer transfer) {
        this.transfer = transfer;
    }

    public static class Jwt {
        private String secret;
        private Long expirationMs;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public Long getExpirationMs() {
            return expirationMs;
        }

        public void setExpirationMs(Long expirationMs) {
            this.expirationMs = expirationMs;
        }
    }

    public static class Transfer {
        private BigDecimal approvalThreshold;

        public BigDecimal getApprovalThreshold() {
            return approvalThreshold;
        }

        public void setApprovalThreshold(BigDecimal approvalThreshold) {
            this.approvalThreshold = approvalThreshold;
        }
    }
}
