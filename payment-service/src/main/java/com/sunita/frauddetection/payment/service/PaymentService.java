package com.sunita.frauddetection.payment.service;

import com.sunita.frauddetection.payment.dto.PaymentRequest;
import com.sunita.frauddetection.payment.dto.PaymentEvent;
import com.sunita.frauddetection.payment.entity.Payment;
import com.sunita.frauddetection.payment.entity.IdempotencyKey;
import com.sunita.frauddetection.payment.repository.PaymentRepository;
import com.sunita.frauddetection.payment.repository.IdempotencyKeyRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final IdempotencyKeyRepository idempotencyRepository;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    private final StripeService stripeService;

    private static final String PAYMENT_TOPIC = "payments";

    public PaymentService(PaymentRepository paymentRepository,
                          IdempotencyKeyRepository idempotencyRepository,
                          KafkaTemplate<String, PaymentEvent> kafkaTemplate,
                          StripeService stripeService) {
        this.paymentRepository = paymentRepository;
        this.idempotencyRepository = idempotencyRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.stripeService = stripeService;
    }

    @Transactional
    public PaymentEvent processPayment(PaymentRequest request) {

        String key = request.getIdempotencyKey();

        Optional<IdempotencyKey> existing = idempotencyRepository.findById(key);

        if (existing.isPresent()) {
            Payment payment = paymentRepository
                    .findByTransactionId(existing.get().getTransactionId())
                    .orElseThrow();

            return mapToEvent(payment, key);
        }

        try {
            var intent = stripeService.createPaymentIntent(request.getAmount());

            String txnId = UUID.randomUUID().toString();

            Payment payment = new Payment(
                    txnId,
                    request.getUserId(),
                    request.getAmount(),
                    "CREATED"
            );

            payment.setStripePaymentIntentId(intent.getId());

            Payment saved = paymentRepository.save(payment);

            idempotencyRepository.save(new IdempotencyKey(key, txnId));

            PaymentEvent event = mapToEvent(saved, key);

            kafkaTemplate.send(PAYMENT_TOPIC, txnId, event);

            return event;

        } catch (Exception e) {
            throw new RuntimeException("Stripe payment failed", e);
        }
    }

    // 🔥 NEW METHOD (CLEAN ARCHITECTURE)
    public Payment getPaymentStatus(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    private PaymentEvent mapToEvent(Payment payment, String key) {
        return new PaymentEvent(
                payment.getTransactionId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt(),
                key,
                payment.getStripePaymentIntentId()
        );
    }
}