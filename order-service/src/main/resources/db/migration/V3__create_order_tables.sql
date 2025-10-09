CREATE TABLE orders (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    address_id VARCHAR(36),
    delivery_method VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    notes TEXT DEFAULT '',

    -- Numeric Values
    shipping_cost NUMERIC(10,2) NOT NULL,
    tax_amount NUMERIC(10,2) NOT NULL,

    -- Shipping
    delivery_tracking_number VARCHAR(100),
    delivery_attempt INTEGER NOT NULL DEFAULT 0,
    days_since_ready_for_pickup INTEGER NOT NULL DEFAULT 0,

    -- Payment
    payment_id VARCHAR(36),

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    estimated_delivery_date TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_orders_address FOREIGN KEY (address_id) REFERENCES addresses(id),
    CONSTRAINT chk_delivery_method CHECK (delivery_method IN ('store_pickup', 'standard_delivery' ,'express_delivery')),
    CONSTRAINT chk_order_status CHECK (status IN ('pending', 'confirmed', 'preparing', 'ready_for_pickup', 'out_for_delivery', 'delivered', 'picked_up', 'cancelled', 'returned')),
    CONSTRAINT chk_shipping_cost_positive CHECK (shipping_cost >= 0),
    CONSTRAINT chk_tax_amount_positive CHECK (tax_amount >= 0),
    CONSTRAINT chk_delivery_attempt_positive CHECK (delivery_attempt >= 0),
    CONSTRAINT chk_days_since_pickup_positive CHECK (days_since_ready_for_pickup >= 0)
);

-- Indexes
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_customer_status ON orders(user_id, status);
CREATE INDEX idx_orders_customer_created_at ON orders(user_id, created_at DESC);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);
CREATE INDEX idx_orders_address_id ON orders(address_id);
CREATE INDEX idx_orders_payment_id ON orders(payment_id);
CREATE INDEX idx_orders_tracking_number ON orders(delivery_tracking_number);


COMMENT ON TABLE orders IS 'Main table for customer orders';
COMMENT ON COLUMN orders.id IS 'Unique identifier for each order (UUID format)';
COMMENT ON COLUMN orders.user_id IS 'Client identifier (UUID format)';
COMMENT ON COLUMN orders.status IS 'Current status of the order';
COMMENT ON COLUMN orders.shipping_cost IS 'Cost of shipping for the order';
COMMENT ON COLUMN orders.tax_amount IS 'Tax amount applied to the order';
COMMENT ON COLUMN orders.delivery_tracking_number IS 'Tracking number for delivery';
COMMENT ON COLUMN orders.delivery_attempt IS 'Number of delivery attempts made';
COMMENT ON COLUMN orders.days_since_ready_for_pickup IS 'Days since order was ready for pickup';
COMMENT ON COLUMN orders.payment_id IS 'Payment identifier (no foreign key constraint)';

CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    subtotal NUMERIC(10,2) NOT NULL,
    quantity INTEGER NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'MXN',
    is_prescription_required BOOLEAN DEFAULT FALSE,

    -- Foreign key
    CONSTRAINT fk_order_items_id FOREIGN KEY (order_id) REFERENCES orders(id),

    -- Constraints
    CONSTRAINT chk_subtotal_positive CHECK (subtotal >= 0),
    CONSTRAINT chk_quantity_positive CHECK (quantity > 0)
);

-- Indexes
CREATE INDEX idx_order_items_id ON order_items(id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);

COMMENT ON TABLE order_items IS 'Items/products for each order';
COMMENT ON COLUMN order_items.is_prescription_required IS 'Indicates if the product requires a prescription';

-- Function to update updated_at column on orders table
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to call the function before updating a row in orders table
CREATE TRIGGER trigger_orders_updated_at
    BEFORE UPDATE ON orders
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE OR REPLACE FUNCTION get_customer_order_count(p_user_id VARCHAR)
RETURNS BIGINT AS $$
BEGIN
    RETURN (SELECT COUNT(*) FROM orders WHERE user_id = p_user_id);
END;
$$ LANGUAGE plpgsql;



-- Index for queries filtering by user_id and date range
CREATE INDEX idx_orders_customer_date_range ON orders(user_id, created_at DESC)
WHERE created_at IS NOT NULL;

-- Partial Index for active orders (not delivered or cancelled)
CREATE INDEX idx_orders_active ON orders(user_id, created_at DESC)
WHERE status NOT IN ('DELIVERED', 'CANCELLED');

-- Index to optimize recent orders retrieval
CREATE INDEX idx_orders_recent ON orders(user_id, created_at DESC)
INCLUDE (id, status);