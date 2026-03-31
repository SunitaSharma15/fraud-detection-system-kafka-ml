package com.sunita.frauddetection.payment.service;

import com.stripe.model.PaymentIntent;
import com.sunita.frauddetection.payment.dto.PaymentEvent;
import com.sunita.frauddetection.payment.entity.Payment;
import com.sunita.frauddetection.payment.repository.PaymentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReconciliationService {

    private static final Logger log = LoggerFactory.getLogger(ReconciliationService.class);

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    private static final String TOPIC = "payment-status";

    public ReconciliationService(PaymentRepository paymentRepository,
                                 KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Reconcile DB payments with Stripe
     */
    public void reconcilePayments() {

        log.info("Starting reconciliation job...");

        List<Payment> payments = paymentRepository.findAll();

        for (Payment payment : payments) {

            try {
                String stripeId = payment.getStripePaymentIntentId();

                if (stripeId == null) {
                    log.warn("Skipping payment with null Stripe ID txnId={}", payment.getTransactionId());
                    continue;
                }

                // 🔥 Fetch latest status from Stripe
                PaymentIntent intent = PaymentIntent.retrieve(stripeId);

                String stripeStatus = intent.getStatus(); // succeeded / requires_payment_method
                String mappedStatus = mapStatus(stripeStatus);

                // 🔥 Compare DB vs Stripe
                if (!payment.getStatus().equals(mappedStatus)) {

                    log.info("Recon txnId={}, DB status={}, Stripe status={}",
                            payment.getTransactionId(),
                            payment.getStatus(),
                            mappedStatus);

                    payment.setStatus(mappedStatus);
                    paymentRepository.save(payment);

                    kafkaTemplate.send("payment-status",
                            payment.getTransactionId(),
                            mapToEvent(payment));
                }


            } catch (Exception e) {
                log.error("Reconciliation failed for txnId={}", payment.getTransactionId(), e);
            }
        }

        log.info("Reconciliation job completed");
    }

    /**
     * Map Stripe status → internal status
     */
    private String mapStatus(String stripeStatus) {

        switch (stripeStatus) {
            case "succeeded":
                return "SUCCESS";
            case "requires_payment_method":
                return "FAILED";
            default:
                return "CREATED";
        }
    }

    private PaymentEvent mapToEvent(Payment payment) {
        return new PaymentEvent(
                payment.getTransactionId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt(),
                null,
                payment.getStripePaymentIntentId()
        );
    }
}