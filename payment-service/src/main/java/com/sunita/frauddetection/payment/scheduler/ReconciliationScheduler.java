package com.sunita.frauddetection.payment.scheduler;

import com.sunita.frauddetection.payment.service.ReconciliationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReconciliationScheduler {

    private final ReconciliationService reconciliationService;

    public ReconciliationScheduler(ReconciliationService reconciliationService) {
        this.reconciliationService = reconciliationService;
    }

    // 🔥 Runs every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void runReconciliation() {
        reconciliationService.reconcilePayments();
    }
}