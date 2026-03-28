package com.sunita.frauddetection.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class StripeConfig {

    @Value("${stripe.api-key}")
    private String apiKey;

    @PostConstruct
    public void init() {
        com.stripe.Stripe.apiKey = apiKey;
    }
}