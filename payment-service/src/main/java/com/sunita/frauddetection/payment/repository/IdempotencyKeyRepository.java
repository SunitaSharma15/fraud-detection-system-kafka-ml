package com.sunita.frauddetection.payment.repository;

import com.sunita.frauddetection.payment.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, String> {
    boolean existsById(String id);
}