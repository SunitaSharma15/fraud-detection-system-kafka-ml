package com.sunita.frauddetection.payment.service;

import com.stripe.model.PaymentIntent;
import com.sunita.frauddetection.payment.dto.PaymentEvent;
import com.sunita.frauddetection.payment.entity.Payment;
import com.sunita.frauddetection.payment.repository.PaymentRepository;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WebhookService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    private static final String TOPIC = "payment-status";

    public WebhookService(PaymentRepository paymentRepository,
                          KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void handleSuccess(PaymentIntent intent) {

        Optional<Payment> paymentOpt = paymentRepository
                .findByStripePaymentIntentId(intent.getId());

        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus("SUCCESS");

            paymentRepository.save(payment);

            // ✅ Convert to event
            PaymentEvent event = mapToEvent(payment);

            kafkaTemplate.send(TOPIC, payment.getTransactionId(), event);
        }
    }

    public void handleFailure(PaymentIntent intent) {

        Optional<Payment> paymentOpt = paymentRepository
                .findByStripePaymentIntentId(intent.getId());

        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus("FAILED");

            paymentRepository.save(payment);

            // ✅ Convert to event
            PaymentEvent event = mapToEvent(payment);

            kafkaTemplate.send(TOPIC, payment.getTransactionId(), event);
        }
    }

    // 🔥 Mapping method (IMPORTANT)
    private PaymentEvent mapToEvent(Payment payment) {
        return new PaymentEvent(
                payment.getTransactionId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt(),
                null, // idempotencyKey not needed here
                payment.getStripePaymentIntentId()
        );
    }
}