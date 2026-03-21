CREATE TABLE payments (
    payment_id VARCHAR PRIMARY KEY,
    amount DECIMAL,
    status VARCHAR,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE idempotency_keys (
    id VARCHAR PRIMARY KEY,
    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);