package com.sunita.frauddetection.payment.controller;

import org.springframework.web.bind.annotation.*;

import com.sunita.frauddetection.payment.dto.PaymentEvent;
import com.sunita.frauddetection.payment.dto.PaymentRequest;
import com.sunita.frauddetection.payment.entity.Payment;
import com.sunita.frauddetection.payment.service.PaymentService;
import com.sunita.frauddetection.payment.service.ReconciliationService;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;
    
    private final ReconciliationService reconciliationService;

    public PaymentController(PaymentService service,
            ReconciliationService reconciliationService) {
this.service = service;
this.reconciliationService = reconciliationService;
}


    // 🔥 Create Payment
    @PostMapping
    public PaymentEvent createPayment(@RequestBody PaymentRequest request) {
        return service.processPayment(request);
    }

    // 🔥 Get Status (CLEAN)
    @GetMapping("/{transactionId}")
    public Payment getStatus(@PathVariable String transactionId) {
        return service.getPaymentStatus(transactionId);
    }
    
    // 🔥 ADD THIS (TEMP TEST API)
    @GetMapping("/reconcile")
    public String reconcile() {
        reconciliationService.reconcilePayments();
        return "Reconciliation completed";
    }
}