package com.sunita.frauddetection.payment.dto;


import java.math.BigDecimal;

public class PaymentRequest {
    private String transactionId;
    private String userId;
    private BigDecimal amount;
    
    public PaymentRequest() {
		// TODO Auto-generated constructor stub
	}

	public PaymentRequest(String transactionId, String userId, BigDecimal amount) {
		super();
		this.transactionId = transactionId;
		this.userId = userId;
		this.amount = amount;
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
    
    

    // getters & setters
}