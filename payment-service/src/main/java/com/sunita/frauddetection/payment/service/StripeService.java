package com.sunita.frauddetection.payment.service;

import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripeService {

	public PaymentIntent createPaymentIntent(BigDecimal amount) throws Exception {

	    long amountInPaise = amount.multiply(new BigDecimal("100")).longValue();

	    PaymentIntentCreateParams params =
	            PaymentIntentCreateParams.builder()
	                    .setAmount(amountInPaise)
	                    .setCurrency("inr")

	                    // 🔥 IMPORTANT FIX
	                    .setAutomaticPaymentMethods(
	                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
	                                    .setEnabled(true)
	                                    .setAllowRedirects(
	                                            PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER
	                                    )
	                                    .build()
	                    )

	                    .build();

	    return PaymentIntent.create(params);
	}
}