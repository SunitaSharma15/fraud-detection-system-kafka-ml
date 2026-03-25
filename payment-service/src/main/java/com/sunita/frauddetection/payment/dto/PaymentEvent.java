package com.sunita.frauddetection.payment.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class PaymentEvent {
    private String transactionId;
    private String userId;
    private BigDecimal amount;
    private String status;
    private OffsetDateTime createdAt;

    public PaymentEvent(String transactionId, String userId, BigDecimal amount, String status, OffsetDateTime createdAt) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
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
    

    // getters & setters
}