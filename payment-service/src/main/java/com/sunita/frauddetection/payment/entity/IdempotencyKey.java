package com.sunita.frauddetection.payment.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKey {

    @Id
    private String id;

    private String transactionId;

    private OffsetDateTime processedAt;

    public IdempotencyKey() {
        this.processedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public IdempotencyKey(String id, String transactionId) {
        this.id = id;
        this.transactionId = transactionId;
        this.processedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    // getters/setters


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public OffsetDateTime getProcessedAt() {
		return processedAt;
	}

	public void setProcessedAt(OffsetDateTime processedAt) {
		this.processedAt = processedAt;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
    
    

   
}