package com.sunita.frauddetection.payment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;

import com.sunita.frauddetection.payment.service.WebhookService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook/stripe")
public class StripeWebhookController {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    private final WebhookService webhookService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public StripeWebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping
    public String handleWebhook(@RequestBody String payload,
                                @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            // 1️⃣ Verify signature
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            log.info("Received Stripe event: {}", event.getType());

            switch (event.getType()) {

                case "payment_intent.succeeded":
                    handleSuccess(payload);
                    break;

                case "payment_intent.payment_failed":
                    handleFailure(payload);
                    break;

                default:
                    log.info("Unhandled event type: {}", event.getType());
            }

            return "success";

        } catch (Exception e) {
            log.error("Webhook processing failed", e);
            throw new RuntimeException("Webhook error", e);
        }
    }

    // 🔥 SUCCESS HANDLER
    private void handleSuccess(String payload) {

        try {
            String paymentIntentId = extractPaymentIntentId(payload);

            log.info("PaymentIntent SUCCESS ID: {}", paymentIntentId);

            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

            webhookService.handleSuccess(intent);

        } catch (Exception e) {
            log.error("Failed to process SUCCESS webhook", e);
        }
    }

    // 🔥 FAILURE HANDLER
    private void handleFailure(String payload) {

        try {
            String paymentIntentId = extractPaymentIntentId(payload);

            log.info("PaymentIntent FAILURE ID: {}", paymentIntentId);

            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

            webhookService.handleFailure(intent);

        } catch (Exception e) {
            log.error("Failed to process FAILURE webhook", e);
        }
    }

    // 🔥 JSON Extractor (KEY PART)
    private String extractPaymentIntentId(String payload) throws Exception {

        JsonNode root = objectMapper.readTree(payload);

        // Navigate JSON safely
        return root.path("data")
                   .path("object")
                   .path("id")
                   .asText();
    }
}