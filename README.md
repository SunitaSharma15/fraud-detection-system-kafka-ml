# Fraud Detection System

## Day 1 — Infrastructure
### Start Infrastructure
docker-compose up -d

### Services
- Kafka: localhost:9092
- PostgreSQL: localhost:5432

### Kafka Topics
- payments (3 partitions)
- payment-status (3 partitions)

### Database Tables
- payments
- idempotency_keys

---

## Day 2 — Payment Service (Producer)

### Overview
Spring Boot microservice for BFSI-grade payments:
- Accepts payment requests
- Stores in PostgreSQL
- Implements idempotency for retries
- Publishes events to Kafka

### Folder
`payment-service/`

### Run Service
```bash
cd payment-service
mvn clean install
mvn spring-boot:run

API Endpoint

POST /payments

Sample Request:

{
  "transactionId": "tx101",
  "userId": "user1",
  "amount": 1000
}
Flow

[Client/API] → [Payment Service] → PostgreSQL → Kafka → payments topic

Features
Idempotency via idempotency_keys table
UTC timestamps
Transactional safety
Logging for audit
Kafka publishing with partition key
Architecture (Day-2)

[Client] → [Payment Service]
├── PostgreSQL (payments + idempotency_keys)
└── Kafka (payments topic)