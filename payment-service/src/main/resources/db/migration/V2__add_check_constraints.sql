-- ============================================================
-- Migration: V2__add_check_constraints.sql
-- Adds CHECK constraints for enum columns to enforce valid values at DB level.
-- This is a secondary safety net — the primary guard is the domain layer.
-- ============================================================

-- payments.status valid values
ALTER TABLE payments
    ADD CONSTRAINT chk_payments_status CHECK (
        status IN (
            'PENDING', 'PROCESSING', 'COMPLETED',
            'FAILED', 'CANCELLED', 'REFUNDED',
            'PARTIALLY_REFUNDED'
        )
    );

-- payments.payment_method valid values
ALTER TABLE payments
    ADD CONSTRAINT chk_payments_payment_method CHECK (
        payment_method IN (
            'CREDIT_CARD', 'DEBIT_CARD', 'BANK_TRANSFER',
            'CASH', 'INTERNAL_CREDIT', 'DIGITAL_WALLET', 'OTHER'
        )
    );

-- payments.gateway valid values
ALTER TABLE payments
    ADD CONSTRAINT chk_payments_gateway CHECK (
        gateway IN ('STRIPE', 'PAYPAL', 'MANUAL', 'NONE', 'OTHER')
    );

-- payments.currency — ISO 4217 codes used in the platform
ALTER TABLE payments
    ADD CONSTRAINT chk_payments_currency CHECK (
        currency IN ('USD', 'MXN', 'EUR', 'GBP')
    );

-- payments amount consistency
ALTER TABLE payments
    ADD CONSTRAINT chk_payments_amount_positive CHECK (amount > 0);

ALTER TABLE payments
    ADD CONSTRAINT chk_payments_refunded_not_exceed CHECK (refunded_amount <= amount);

-- sales.status valid values
ALTER TABLE sales
    ADD CONSTRAINT chk_sales_status CHECK (
        status IN (
            'CONFIRMED', 'CANCELLED',
            'REFUNDED', 'PARTIALLY_REFUNDED'
        )
    );

-- sales.currency
ALTER TABLE sales
    ADD CONSTRAINT chk_sales_currency CHECK (
        currency IN ('USD', 'MXN', 'EUR', 'GBP')
    );

-- sales amount consistency
ALTER TABLE sales
    ADD CONSTRAINT chk_sales_amount_positive CHECK (total_amount > 0);

ALTER TABLE sales
    ADD CONSTRAINT chk_sales_refunded_not_exceed CHECK (refunded_amount <= total_amount);
