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

    /**
     * Production-grade payment processing
     */
    @Transactional
    public PaymentEvent processPayment(PaymentRequest request) {

        String idempotencyKey = request.getIdempotencyKey();

        log.info("Processing payment request for idempotencyKey={}", idempotencyKey);

        // 1️⃣ Idempotency check
        Optional<IdempotencyKey> existing = idempotencyRepository.findById(idempotencyKey);

        if (existing.isPresent()) {
            log.info("Duplicate request detected for idempotencyKey={}", idempotencyKey);

            Payment existingPayment = paymentRepository
                    .findByTransactionId(existing.get().getTransactionId())
                    .orElseThrow(() ->
                            new IllegalStateException("Payment missing for existing idempotency key"));

            return mapToEvent(existingPayment, idempotencyKey);
        }

        try {
            // 2️⃣ Call Stripe (PaymentIntent)
            var paymentIntent = stripeService.createPaymentIntent(request.getAmount());

            // 3️⃣ Generate transactionId (server controlled)
            String transactionId = UUID.randomUUID().toString();

            // 4️⃣ Persist payment
            Payment payment = new Payment(
                    transactionId,
                    request.getUserId(),
                    request.getAmount(),
                    "CREATED"
            );

            payment.setStripePaymentIntentId(paymentIntent.getId());

            Payment savedPayment = paymentRepository.save(payment);

            log.info("Payment saved: txnId={}, stripeIntentId={}",
                    transactionId, paymentIntent.getId());

            // 5️⃣ Save idempotency mapping
            idempotencyRepository.save(
                    new IdempotencyKey(idempotencyKey, transactionId)
            );

            // 6️⃣ Publish Kafka event
            PaymentEvent event = mapToEvent(savedPayment, idempotencyKey);

            kafkaTemplate.send(PAYMENT_TOPIC, transactionId, event);

            log.info("Kafka event published for txnId={}", transactionId);

            return event;

        } catch (Exception e) {
            log.error("Stripe payment failed for idempotencyKey={}", idempotencyKey, e);
            throw new RuntimeException("Stripe payment failed", e);
        }
    }

    /**
     * Map entity → Kafka event
     */
    private PaymentEvent mapToEvent(Payment payment, String idempotencyKey) {
        return new PaymentEvent(
                payment.getTransactionId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt(),
                idempotencyKey,
                payment.getStripePaymentIntentId()
        );
    }
}