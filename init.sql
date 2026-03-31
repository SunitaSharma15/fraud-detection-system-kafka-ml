CREATE TABLE payments (
    id SERIAL PRIMARY KEY,
    transaction_id VARCHAR UNIQUE NOT NULL,
    user_id VARCHAR NOT NULL,
    amount DECIMAL NOT NULL,
    status VARCHAR NOT NULL,
    stripe_payment_intent_id VARCHAR,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE idempotency_keys (
    id VARCHAR PRIMARY KEY,
    transaction_id VARCHAR,
    processed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);