package com.sunita.frauddetection.payment.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKey {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;  // typically the same as transactionId

    @Column(nullable = false)
    private OffsetDateTime processedAt;

    public IdempotencyKey() {
        this.processedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public IdempotencyKey(String id) {
        this.id = id;
        this.processedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

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
    
    

    // getters & setters
}