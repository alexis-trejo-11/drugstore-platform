-- ============================================================
-- Migration: V1__create_payments_and_sales_tables.sql
-- Service:   payment-service
-- Author:    drugstore-platform
-- ============================================================

-- ─────────────────────────────────────────────────────────────
-- TABLE: payments
-- ─────────────────────────────────────────────────────────────
CREATE TABLE payments (
    id                      VARCHAR(36)     NOT NULL,
    order_id                VARCHAR(36)     NOT NULL,
    customer_id             VARCHAR(36)     NOT NULL,

    -- Money
    amount                  DECIMAL(19, 2)  NOT NULL,
    currency                VARCHAR(3)      NOT NULL,
    refunded_amount         DECIMAL(19, 2)  NOT NULL DEFAULT 0.00,

    -- Status & method
    status                  VARCHAR(30)     NOT NULL,
    payment_method          VARCHAR(30)     NOT NULL,

    -- Gateway tracking (Stripe)
    gateway                 VARCHAR(20)     NOT NULL DEFAULT 'NONE',
    gateway_payment_id      VARCHAR(100)    NULL,       -- Stripe PaymentIntent ID (pi_xxxxx)
    gateway_charge_id       VARCHAR(100)    NULL,       -- Stripe Charge ID (ch_xxxxx)

    -- Refund info (denormalized — one refund per payment for now)
    refund_reason           VARCHAR(500)    NULL,
    refund_gateway_refund_id VARCHAR(100)   NULL,       -- Stripe Refund ID (re_xxxxx)
    refunded_at             TIMESTAMP       NULL,

    -- Timestamps
    created_at              TIMESTAMP       NOT NULL,
    updated_at              TIMESTAMP       NOT NULL,
    completed_at            TIMESTAMP       NULL,
    deleted_at              TIMESTAMP       NULL,       -- soft delete support

    -- Optimistic locking
    version                 BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_payments PRIMARY KEY (id)
);

-- Unique: one payment per order
ALTER TABLE payments
    ADD CONSTRAINT uq_payments_order_id UNIQUE (order_id);

-- Fast lookup by Stripe PaymentIntent ID (used in webhook processing)
CREATE UNIQUE INDEX idx_payments_gateway_payment_id
    ON payments (gateway_payment_id)
    WHERE gateway_payment_id IS NOT NULL;

-- Query patterns: by customer, by status
CREATE INDEX idx_payments_customer_id ON payments (customer_id);
CREATE INDEX idx_payments_status      ON payments (status);

-- Composite: customer's payments filtered by status (e.g. "my active payments")
CREATE INDEX idx_payments_customer_status ON payments (customer_id, status);

-- ─────────────────────────────────────────────────────────────
-- TABLE: sales
-- ─────────────────────────────────────────────────────────────
CREATE TABLE sales (
    id                      VARCHAR(36)     NOT NULL,
    payment_id              VARCHAR(36)     NOT NULL,   -- FK reference (no DB constraint — aggregate boundary)
    order_id                VARCHAR(36)     NOT NULL,
    customer_id             VARCHAR(36)     NOT NULL,

    -- Money
    total_amount            DECIMAL(19, 2)  NOT NULL,
    currency                VARCHAR(3)      NOT NULL,
    refunded_amount         DECIMAL(19, 2)  NOT NULL DEFAULT 0.00,

    -- Status
    status                  VARCHAR(30)     NOT NULL,

    -- Timestamps
    created_at              TIMESTAMP       NOT NULL,
    updated_at              TIMESTAMP       NOT NULL,
    cancelled_at            TIMESTAMP       NULL,

    -- Optimistic locking
    version                 BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_sales PRIMARY KEY (id)
);

-- Unique: one sale per payment, one sale per order
ALTER TABLE sales
    ADD CONSTRAINT uq_sales_payment_id UNIQUE (payment_id);

ALTER TABLE sales
    ADD CONSTRAINT uq_sales_order_id UNIQUE (order_id);

-- Query patterns
CREATE INDEX idx_sales_customer_id    ON sales (customer_id);
CREATE INDEX idx_sales_status         ON sales (status);
CREATE INDEX idx_sales_customer_status ON sales (customer_id, status);

-- ─────────────────────────────────────────────────────────────
-- NOTE: No FK between sales.payment_id → payments.id
-- Aggregate roots are independent — referential integrity
-- is enforced at the application/domain layer, not the DB.
-- This allows each aggregate to be deployed or scaled independently.
-- ─────────────────────────────────────────────────────────────
