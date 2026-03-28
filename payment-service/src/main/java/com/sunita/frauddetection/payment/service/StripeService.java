package com.sunita.frauddetection.payment.service;


import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripeService {

    @Value("${stripe.api-key}")
    private String apiKey;

    public PaymentIntent createPaymentIntent(BigDecimal amount) throws Exception {

        // initialize stripe
        Stripe.apiKey = apiKey;

        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amount.multiply(new BigDecimal("100")).longValue()) // INR → paise
                        .setCurrency("inr")
                        .build();

        return PaymentIntent.create(params);
    }
}