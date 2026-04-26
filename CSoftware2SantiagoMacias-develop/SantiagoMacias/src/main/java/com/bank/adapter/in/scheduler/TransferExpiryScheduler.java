package com.bank.adapter.in.scheduler;

import com.bank.application.port.input.TransferInputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ADAPTER (Driving) - TransferExpiryScheduler
 *
 * Periodically checks for transfers that have been pending approval for
 * more than 60 minutes and expires them automatically.
 *
 * This is a driving adapter — it triggers the use case just like a REST controller would.
 */
@Component
public class TransferExpiryScheduler {

    private static final Logger log = LoggerFactory.getLogger(TransferExpiryScheduler.class);

    private final TransferInputPort transferInputPort;

    public TransferExpiryScheduler(TransferInputPort transferInputPort) {
        this.transferInputPort = transferInputPort;
    }

    // Runs every 5 minutes
    @Scheduled(fixedDelay = 300_000)
    public void expireStaleTransfers() {
        int expired = transferInputPort.processExpiredTransfers();
        if (expired > 0) {
            log.info("[SCHEDULER] Auto-expired {} pending transfer(s) older than 60 minutes.", expired);
        }
    }
}
