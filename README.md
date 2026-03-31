### Fraud Detection System: A BFSI-grade payment processing system built using:

Spring Boot
Apache Kafka
PostgreSQL
Stripe

It demonstrates event-driven architecture, idempotent payments, webhook handling, and reconciliation for data consistency.

Day 1 — Infrastructure Setup
 Start Infrastructure
docker-compose up -d

 Services
Kafka → localhost:9092
PostgreSQL → localhost:5432

Kafka Topics
payments (payment creation events)
payment-status (status updates)

Database Tables
payments
idempotency_keys
-----------------------------------------------------------------
Day 2 — Payment Service (Producer)
Overview

A Spring Boot microservice that:

Accepts payment requests
Stores data in PostgreSQL
Implements idempotency (retry-safe)
Publishes events to Kafka

Run Service
cd payment-service
mvn clean install
mvn spring-boot:run

API
==================================
POST /payments
Sample Request
{
  "idempotencyKey": "test123",
  "userId": "user1",
  "amount": 1000
}

Flow

Client → Payment Service → PostgreSQL → Kafka (payments)

Features
Idempotency using idempotency_keys
UTC timestamps (audit safe)
Transactional consistency
Kafka event publishing
Structured logging

Architecture
====================================================================
Client → Payment Service
        ↓
   PostgreSQL
        ↓
     Kafka (payments)

Day 3 — Stripe Integration & Webhook
-----------------------------------------------------------------
Overview

Integrated Stripe for real payment processing:

PaymentIntent creation
Backend-based confirmation (test mode)
Webhook-based asynchronous updates
Event-driven status propagation

Payment Flow
---------------------------------------------
Client → Payment API → Stripe PaymentIntent
        ↓
   Payment Confirmation
        ↓
Stripe Webhook → Backend
        ↓
DB updated (SUCCESS / FAILED)
        ↓
Kafka → payment-status

Key Components
========================================================
1. StripeService
Creates PaymentIntent
Converts amount → paise
Uses test payment method (pm_card_visa)

2. StripeWebhookController
Receives Stripe events
Extracts PaymentIntent ID
Handles:
payment_intent.succeeded
payment_intent.payment_failed

3. WebhookService
Updates DB status
Publishes Kafka events

Webhook Setup
stripe listen --forward-to localhost:8081/webhook/stripe

Environment Variables
export STRIPE_API_KEY=sk_test_xxx
export STRIPE_WEBHOOK_SECRET=whsec_xxx

Testing Flow
------------------------------------------------
Call API: POST /payments

Response:
{
  "transactionId": "...",
  "stripePaymentIntentId": "pi_xxx",
  "status": "CREATED"
}
Confirm payment:
stripe payment_intents confirm pi_xxx --payment-method pm_card_visa

Expected Result
--------------------------------------------------
Stage	Status
After API	CREATED
After webhook	SUCCESS

Day 4 — Reconciliation (Critical BFSI Feature)
============================================================================
Overview:  Reconciliation ensures consistency between Stripe and database.

Why Needed

Handles:

Missed webhooks
Network failures
Data inconsistencies
Partial updates

Architecture
-----------------------------------------------
Scheduler → Reconciliation Service
           ↓
     Fetch DB payments
           ↓
     Call Stripe API
           ↓
     Compare status
           ↓
     Fix mismatches
           ↓
     Kafka event

Reconciliation Flow
------------------------------------------------------
DB (CREATED) 
Stripe (SUCCESS) 
        ↓
Reconciliation
        ↓
DB updated → SUCCESS 

Testing
GET /payments/reconcile

Example Scenario
Webhook fails 
↓
Reconciliation runs 
↓
System self-heals 

Kafka Topics (Final)
-----------------------------------------------------
Topic	Purpose
payments	Payment creation
payment-status	Status updates

Full System Architecture
-------------------------------------------------------------------
Client → Payment API
        ↓
   PostgreSQL (CREATED)
        ↓
   Stripe PaymentIntent
        ↓
   Webhook → DB update
        ↓
   Kafka → payment-status
        ↓
   Reconciliation (backup consistency)

Key BFSI Concepts Implemented
============================================================
Idempotent payment processing
Event-driven architecture
Stripe PaymentIntent lifecycle
Webhook-based async processing
Reconciliation (critical banking pattern)
Audit-safe timestamps (UTC)

### Future Enhancements
Fraud Detection Service (Kafka Consumer + ML)
AML (Anti-Money Laundering)
Notification Service
Monitoring Dashboard