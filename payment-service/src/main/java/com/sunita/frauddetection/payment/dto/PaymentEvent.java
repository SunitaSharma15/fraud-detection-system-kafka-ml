package com.sunita.frauddetection.payment.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class PaymentEvent {

    private String transactionId;
    private String userId;
    private BigDecimal amount;
    private String status;
    private OffsetDateTime createdAt;

    private String idempotencyKey;
    private String stripePaymentIntentId;

    public PaymentEvent(String transactionId,
                        String userId,
                        BigDecimal amount,
                        String status,
                        OffsetDateTime createdAt,
                        String idempotencyKey,
                        String stripePaymentIntentId) {

        this.transactionId = transactionId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.idempotencyKey = idempotencyKey;
        this.stripePaymentIntentId = stripePaymentIntentId;
    }

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}

	public String getStripePaymentIntentId() {
		return stripePaymentIntentId;
	}

	public void setStripePaymentIntentId(String stripePaymentIntentId) {
		this.stripePaymentIntentId = stripePaymentIntentId;
	}

   
}