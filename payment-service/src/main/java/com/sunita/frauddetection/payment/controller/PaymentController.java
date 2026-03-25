package com.sunita.frauddetection.payment.controller;

import com.sunita.frauddetection.payment.dto.PaymentEvent;
import com.sunita.frauddetection.payment.dto.PaymentRequest;
import com.sunita.frauddetection.payment.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public PaymentEvent createPayment(@RequestBody PaymentRequest request) {
        return service.processPayment(request);
    }
}