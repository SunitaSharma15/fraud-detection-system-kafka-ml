package com.sunita.frauddetection.payment.dto;


import java.math.BigDecimal;

public class PaymentRequest {

    private String idempotencyKey;
    private String userId;
    private BigDecimal amount;
    
    public PaymentRequest() {
		// TODO Auto-generated constructor stub
	}

	public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
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

   
}