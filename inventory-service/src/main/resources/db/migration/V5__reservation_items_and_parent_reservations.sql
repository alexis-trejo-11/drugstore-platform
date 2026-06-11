-- Align DB with JPA: StockReservationsEntity (header) + ReservationItemEntity (lines).
-- Legacy V2 used a flat stock_reservations row per inventory line and omitted reservation_items.

DROP VIEW IF EXISTS active_stock_reservations CASCADE;

DROP TABLE IF EXISTS reservation_items CASCADE;
DROP TABLE IF EXISTS stock_reservations CASCADE;

CREATE TABLE stock_reservations (
    id VARCHAR(36) NOT NULL,
    order_id VARCHAR(36) NOT NULL,
    order_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    expiration_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT pk_stock_reservations PRIMARY KEY (id),
    CONSTRAINT chk_stock_reservations_order_type CHECK (
        order_type IN ('PURCHASE_ORDER', 'SALE_ORDER')
    ),
    CONSTRAINT chk_stock_reservations_status CHECK (
        status IN ('ACTIVE', 'CONFIRMED', 'RELEASED', 'EXPIRED', 'CANCELLED')
    ),
    CONSTRAINT chk_stock_reservations_expiration_future CHECK (expiration_time > created_at)
);

CREATE TABLE reservation_items (
    id VARCHAR(36) NOT NULL,
    reason VARCHAR(500),
    quantity INTEGER NOT NULL,
    reservation_id VARCHAR(36) NOT NULL,
    inventory_id VARCHAR(36) NOT NULL,
    batch_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT pk_reservation_items PRIMARY KEY (id),
    CONSTRAINT chk_reservation_items_positive_quantity CHECK (quantity > 0)
);

CREATE INDEX idx_reservations_order_id ON stock_reservations(order_id);
CREATE INDEX idx_reservations_order_type ON stock_reservations(order_type);
CREATE INDEX idx_reservations_status ON stock_reservations(status);
CREATE INDEX idx_reservations_expiration_time ON stock_reservations(expiration_time);
CREATE INDEX idx_reservations_created_at ON stock_reservations(created_at);
CREATE INDEX idx_reservations_status_expiration ON stock_reservations(status, expiration_time)
    WHERE status IN ('ACTIVE', 'CONFIRMED');

CREATE UNIQUE INDEX idx_reservations_order_active_unique
    ON stock_reservations(order_id, order_type)
    WHERE status IN ('ACTIVE', 'CONFIRMED');

CREATE INDEX idx_reservation_items_reservation_id ON reservation_items(reservation_id);
CREATE INDEX idx_reservation_items_inventory_id ON reservation_items(inventory_id);
CREATE INDEX idx_reservation_items_batch_id ON reservation_items(batch_id);

ALTER TABLE reservation_items
    ADD CONSTRAINT fk_reservation_items_reservation
        FOREIGN KEY (reservation_id) REFERENCES stock_reservations(id) ON DELETE CASCADE;

ALTER TABLE reservation_items
    ADD CONSTRAINT fk_reservation_items_inventory
        FOREIGN KEY (inventory_id) REFERENCES inventories(id) ON DELETE CASCADE;

ALTER TABLE reservation_items
    ADD CONSTRAINT fk_reservation_items_batch
        FOREIGN KEY (batch_id) REFERENCES inventory_batches(id) ON DELETE RESTRICT;

DROP TRIGGER IF EXISTS update_reservation_items_updated_at ON reservation_items;
CREATE TRIGGER update_reservation_items_updated_at
    BEFORE UPDATE ON reservation_items
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_stock_reservations_updated_at ON stock_reservations;
CREATE TRIGGER update_stock_reservations_updated_at
    BEFORE UPDATE ON stock_reservations
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS check_reservation_expiration ON stock_reservations;
CREATE TRIGGER check_reservation_expiration
    AFTER INSERT OR UPDATE ON stock_reservations
    FOR EACH STATEMENT
    EXECUTE FUNCTION expire_old_reservations();

DROP TRIGGER IF EXISTS validate_reservation_status_trigger ON stock_reservations;
CREATE TRIGGER validate_reservation_status_trigger
    BEFORE UPDATE ON stock_reservations
    FOR EACH ROW
    EXECUTE FUNCTION validate_reservation_status();

CREATE OR REPLACE VIEW active_stock_reservations AS
SELECT
    sr.id,
    sr.order_id,
    sr.order_type,
    sr.status,
    sr.expiration_time,
    sr.created_at
FROM stock_reservations sr
WHERE sr.status IN ('ACTIVE', 'CONFIRMED')
  AND sr.expiration_time > CURRENT_TIMESTAMP;

COMMENT ON TABLE stock_reservations IS 'Aggregate stock reservation per order (header)';
COMMENT ON TABLE reservation_items IS 'Per-inventory / per-batch lines for a stock reservation';
