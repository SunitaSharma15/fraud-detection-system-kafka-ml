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

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;
    private final IdempotencyKeyRepository idempotencyRepository;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    private static final String PAYMENT_TOPIC = "payments";

    public PaymentService(PaymentRepository paymentRepository,
                          IdempotencyKeyRepository idempotencyRepository,
                          KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.idempotencyRepository = idempotencyRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Process a payment request in an idempotent, production-safe manner.
     */
    @Transactional
    public PaymentEvent processPayment(PaymentRequest request) {
        // 1️⃣ Check idempotency table
        if (idempotencyRepository.existsById(request.getTransactionId())) {
            log.info("Duplicate request detected for transactionId: {}", request.getTransactionId());
            Payment existingPayment = paymentRepository.findByTransactionId(request.getTransactionId())
                    .orElseThrow(() -> new IllegalStateException("Payment record missing for existing idempotency key"));
            return mapToEvent(existingPayment);
        }

        // 2️⃣ Persist payment
        Payment payment = new Payment(request.getTransactionId(), request.getUserId(), request.getAmount(), "CREATED");
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment saved to DB: {}", savedPayment.getTransactionId());

        // 3️⃣ Insert idempotency key
        IdempotencyKey key = new IdempotencyKey(savedPayment.getTransactionId());
        idempotencyRepository.save(key);
        log.info("Idempotency key saved: {}", key.getId());

        // 4️⃣ Publish to Kafka
        PaymentEvent event = mapToEvent(savedPayment);
        kafkaTemplate.send(PAYMENT_TOPIC, savedPayment.getTransactionId(), event);
        log.info("Payment event published to Kafka topic '{}': {}", PAYMENT_TOPIC, savedPayment.getTransactionId());

        return event;
    }

    /**
     * Map Payment entity to PaymentEvent DTO
     */
    private PaymentEvent mapToEvent(Payment payment) {
        return new PaymentEvent(
                payment.getTransactionId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}