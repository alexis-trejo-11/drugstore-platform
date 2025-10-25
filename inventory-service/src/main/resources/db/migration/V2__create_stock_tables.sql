-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS stock_adjustments CASCADE;
DROP TABLE IF EXISTS stock_reservations CASCADE;

-- Create stock_reservations table
CREATE TABLE IF NOT EXISTS stock_reservations (
    id VARCHAR(36) NOT NULL,
    inventory_id VARCHAR(36) NOT NULL,
    order_id VARCHAR(36) NOT NULL,
    quantity INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    expiration_time TIMESTAMP NOT NULL,
    reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_stock_reservations PRIMARY KEY (id),
    CONSTRAINT chk_reservations_positive_quantity CHECK (quantity > 0),
    CONSTRAINT chk_reservations_expiration_future CHECK (expiration_time > created_at),
    CONSTRAINT chk_reservations_status_values CHECK (
        status IN ('ACTIVE','CONFIRMED','RELEASED','EXPIRED','CANCELLED')
    )
);

-- Create stock_adjustments table
CREATE TABLE IF NOT EXISTS stock_adjustments (
    id VARCHAR(36) NOT NULL,
    inventory_id VARCHAR(36) NOT NULL,
    batch_id VARCHAR(36),
    adjustment_type VARCHAR(20) NOT NULL,
    quantity_before INTEGER NOT NULL,
    quantity_adjusted INTEGER NOT NULL,
    quantity_after INTEGER NOT NULL,
    reason VARCHAR(50) NOT NULL,
    notes TEXT,
    approved_by VARCHAR(36),
    performed_by VARCHAR(36) NOT NULL,
    adjustment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_stock_adjustments PRIMARY KEY (id),
    CONSTRAINT chk_adjustments_positive_quantities CHECK (
        quantity_before >= 0 AND
        quantity_after >= 0
    ),
    CONSTRAINT chk_adjustments_quantity_consistency CHECK (
        (adjustment_type = 'INCREASE' AND quantity_adjusted > 0 AND quantity_after = quantity_before + quantity_adjusted) OR
        (adjustment_type = 'DECREASE' AND quantity_adjusted > 0 AND quantity_after = quantity_before - quantity_adjusted)
    ),
    CONSTRAINT chk_adjustments_quantity_after_non_negative CHECK (quantity_after >= 0),
    CONSTRAINT chk_adjustment_type_values CHECK (
        adjustment_type IN ('INCREASE','DECREASE')
    ),
    CONSTRAINT chk_adjustment_reason_values CHECK (
        reason IN ('PHYSICAL_COUNT','DAMAGED','EXPIRED','THEFT','LOST','QUALITY_CONTROL','SYSTEM_ERROR','RECEIVING_ERROR','RETURNED_GOODS','OTHER')
    )
);

-- Create indexes for stock_reservations
CREATE INDEX IF NOT EXISTS idx_reservations_inventory_id ON stock_reservations(inventory_id);
CREATE INDEX IF NOT EXISTS idx_reservations_order_id ON stock_reservations(order_id);
CREATE INDEX IF NOT EXISTS idx_reservations_status ON stock_reservations(status);
CREATE INDEX IF NOT EXISTS idx_reservations_expiration_time ON stock_reservations(expiration_time);
CREATE INDEX IF NOT EXISTS idx_reservations_created_at ON stock_reservations(created_at);
CREATE INDEX IF NOT EXISTS idx_reservations_status_expiration ON stock_reservations(status, expiration_time)
    WHERE status IN ('ACTIVE', 'CONFIRMED');

-- Create unique index for active reservations per order and inventory
CREATE UNIQUE INDEX IF NOT EXISTS idx_reservations_active_unique
ON stock_reservations(inventory_id, order_id)
WHERE status IN ('ACTIVE', 'CONFIRMED');

-- Create indexes for stock_adjustments
CREATE INDEX IF NOT EXISTS idx_adjustments_inventory_id ON stock_adjustments(inventory_id);
CREATE INDEX IF NOT EXISTS idx_adjustments_batch_id ON stock_adjustments(batch_id);
CREATE INDEX IF NOT EXISTS idx_adjustments_adjustment_type ON stock_adjustments(adjustment_type);
CREATE INDEX IF NOT EXISTS idx_adjustments_reason ON stock_adjustments(reason);
CREATE INDEX IF NOT EXISTS idx_adjustments_adjustment_date ON stock_adjustments(adjustment_date);
CREATE INDEX IF NOT EXISTS idx_adjustments_created_at ON stock_adjustments(created_at);
CREATE INDEX IF NOT EXISTS idx_adjustments_performed_by ON stock_adjustments(performed_by);
CREATE INDEX IF NOT EXISTS idx_adjustments_approved_by ON stock_adjustments(approved_by);

-- Create composite index for common query patterns
CREATE INDEX IF NOT EXISTS idx_adjustments_inventory_date ON stock_adjustments(inventory_id, adjustment_date);
CREATE INDEX IF NOT EXISTS idx_adjustments_type_reason ON stock_adjustments(adjustment_type, reason);

-- Add foreign key constraints (use safe DO blocks to avoid errors if re-run)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_reservations_inventory_id') THEN
        ALTER TABLE stock_reservations
        ADD CONSTRAINT fk_reservations_inventory_id
        FOREIGN KEY (inventory_id) REFERENCES inventories(id)
        ON DELETE CASCADE;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_adjustments_inventory_id') THEN
        ALTER TABLE stock_adjustments
        ADD CONSTRAINT fk_adjustments_inventory_id
        FOREIGN KEY (inventory_id) REFERENCES inventories(id)
        ON DELETE CASCADE;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_adjustments_batch_id') THEN
        ALTER TABLE stock_adjustments
        ADD CONSTRAINT fk_adjustments_batch_id
        FOREIGN KEY (batch_id) REFERENCES inventory_batches(id)
        ON DELETE SET NULL;
    END IF;
END$$;

-- Create trigger function for updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for stock_reservations updated_at
DROP TRIGGER IF EXISTS update_stock_reservations_updated_at ON stock_reservations;
CREATE TRIGGER update_stock_reservations_updated_at
    BEFORE UPDATE ON stock_reservations
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Create function to automatically expire reservations
CREATE OR REPLACE FUNCTION expire_old_reservations()
RETURNS TRIGGER AS $$
BEGIN
    -- Avoid recursion: if we're already in a trigger invocation, do nothing
    IF pg_trigger_depth() > 1 THEN
        RETURN NULL;
    END IF;

    -- Update reservations that have expired
    UPDATE stock_reservations
    SET status = 'EXPIRED', updated_at = CURRENT_TIMESTAMP
    WHERE status IN ('ACTIVE', 'CONFIRMED')
    AND expiration_time <= CURRENT_TIMESTAMP;

    RETURN NULL; -- for statement-level triggers RETURN NULL is appropriate
END;
$$ LANGUAGE plpgsql;

-- Create trigger to check for expired reservations (statement-level to reduce recursion/cost)
DROP TRIGGER IF EXISTS check_reservation_expiration ON stock_reservations;
CREATE TRIGGER check_reservation_expiration
    AFTER INSERT OR UPDATE ON stock_reservations
    FOR EACH STATEMENT
    EXECUTE FUNCTION expire_old_reservations();

-- Create function to validate reservation status transitions
CREATE OR REPLACE FUNCTION validate_reservation_status()
RETURNS TRIGGER AS $$
BEGIN
    -- Prevent invalid status transitions
    IF OLD.status = 'EXPIRED' AND NEW.status != 'EXPIRED' THEN
        RAISE EXCEPTION 'Cannot change status from EXPIRED';
    END IF;

    IF OLD.status = 'CANCELLED' AND NEW.status != 'CANCELLED' THEN
        RAISE EXCEPTION 'Cannot change status from CANCELLED';
    END IF;

    IF OLD.status = 'RELEASED' AND NEW.status != 'RELEASED' THEN
        RAISE EXCEPTION 'Cannot change status from RELEASED';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for reservation status validation
DROP TRIGGER IF EXISTS validate_reservation_status_trigger ON stock_reservations;
CREATE TRIGGER validate_reservation_status_trigger
    BEFORE UPDATE ON stock_reservations
    FOR EACH ROW
    EXECUTE FUNCTION validate_reservation_status();

-- Create function to validate adjustment quantities
CREATE OR REPLACE FUNCTION validate_adjustment_quantities()
RETURNS TRIGGER AS $$
BEGIN
    -- Ensure quantity_after is calculated correctly
    IF NEW.adjustment_type = 'INCREASE' THEN
        IF NEW.quantity_after != NEW.quantity_before + NEW.quantity_adjusted THEN
            RAISE EXCEPTION 'Quantity after must equal quantity before plus quantity adjusted for INCREASE adjustments';
        END IF;
    ELSIF NEW.adjustment_type = 'DECREASE' THEN
        IF NEW.quantity_after != NEW.quantity_before - NEW.quantity_adjusted THEN
            RAISE EXCEPTION 'Quantity after must equal quantity before minus quantity adjusted for DECREASE adjustments';
        END IF;
    END IF;

    -- Ensure quantity_after is not negative
    IF NEW.quantity_after < 0 THEN
        RAISE EXCEPTION 'Quantity after adjustment cannot be negative';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for adjustment quantity validation
DROP TRIGGER IF EXISTS validate_adjustment_quantities_trigger ON stock_adjustments;
CREATE TRIGGER validate_adjustment_quantities_trigger
    BEFORE INSERT OR UPDATE ON stock_adjustments
    FOR EACH ROW
    EXECUTE FUNCTION validate_adjustment_quantities();

-- Create comments for documentation
COMMENT ON TABLE stock_reservations IS 'Stores stock reservations for orders with expiration tracking';
COMMENT ON TABLE stock_adjustments IS 'Audit trail for all stock adjustments with reason and approval tracking';
COMMENT ON COLUMN stock_reservations.expiration_time IS 'When the reservation expires and stock should be released';
COMMENT ON COLUMN stock_adjustments.approved_by IS 'User who approved the adjustment (nullable for self-approved adjustments)';

-- Create a view for active reservations
CREATE OR REPLACE VIEW active_stock_reservations AS
SELECT
    id,
    inventory_id,
    order_id,
    quantity,
    expiration_time,
    reason,
    created_at
FROM stock_reservations
WHERE status IN ('ACTIVE', 'CONFIRMED')
AND expiration_time > CURRENT_TIMESTAMP;

-- Create a view for recent adjustments
CREATE OR REPLACE VIEW recent_stock_adjustments AS
SELECT
    id,
    inventory_id,
    adjustment_type,
    quantity_before,
    quantity_adjusted,
    quantity_after,
    reason,
    adjustment_date,
    performed_by
FROM stock_adjustments
WHERE adjustment_date >= CURRENT_DATE - INTERVAL '30 days'
ORDER BY adjustment_date DESC;

-- Verify table creation (selects are harmless in migrations, but optional)
--
-- SELECT
--    table_name,
--    table_type
-- FROM information_schema.tables
-- WHERE table_schema = 'public'
-- AND table_name IN ('stock_reservations', 'stock_adjustments')
-- ORDER BY table_name;
