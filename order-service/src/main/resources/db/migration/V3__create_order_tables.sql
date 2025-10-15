CREATE TABLE orders (
    id VARCHAR(36) PRIMARY KEY,
    delivery_method VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    notes TEXT DEFAULT '',
    service_fee NUMERIC(10,2) NOT NULL,
    tax_fee NUMERIC(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'MXN',

    -- Relationships
    payment_id VARCHAR(36),
    address_id VARCHAR(36),
    user_id VARCHAR(36) NOT NULL,
    delivery_info_id VARCHAR(36),
    pick_up_info_id VARCHAR(36),

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_orders_address FOREIGN KEY (address_id) REFERENCES addresses(id),
    CONSTRAINT chk_delivery_method CHECK (delivery_method IN ('store_pickup', 'standard_delivery' ,'express_delivery')),
    CONSTRAINT chk_order_status CHECK (status IN ('pending', 'confirmed', 'preparing', 'ready_for_pickup', 'out_for_delivery', 'delivered', 'picked_up', 'cancelled', 'returned'))
);

CREATE TABLE deliveryInfo(
    id VARCHAR(36) PRIMARY KEY,
    tracking_number VARCHAR(100),
    delivery_attempt INTEGER DEFAULT 0,
    estimated_delivery_date TIMESTAMP WITH TIME ZONE,
    actual_delivery_date TIMESTAMP WITH TIME ZONE,
    delivery_cost NUMERIC(10,2),
    order_id VARCHAR(36) NOT NULL,
    address_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_delivery_info_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_delivery_info_address FOREIGN KEY (address_id) REFERENCES addresses(id) ON DELETE SET NULL,
    CONSTRAINT chk_delivery_attempt_positive CHECK (delivery_attempt >= 0),
    CONSTRAINT chk_delivery_cost_positive CHECK (delivery_cost >= 0)
);

CREATE TABLE pickUpInfo(
    id VARCHAR(36) PRIMARY KEY,
    pickup_code VARCHAR(100),
    pickup_date TIMESTAMP WITH TIME ZONE,
    store_id VARCHAR(36) NOT NULL,
    store_name VARCHAR(255),
    store_address VARCHAR(255),
    order_id VARCHAR(36) NOT NULL,
    picked_up_at TIMESTAMP WITH TIME ZONE,
    available_until TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_pickup_info_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Agregar las foreign keys desde orders hacia deliveryInfo y pickUpInfo
ALTER TABLE orders
    ADD CONSTRAINT fk_orders_delivery_info FOREIGN KEY (delivery_info_id) REFERENCES deliveryInfo(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_orders_pickup_info FOREIGN KEY (pick_up_info_id) REFERENCES pickUpInfo(id) ON DELETE SET NULL;

-- Indexes for orders table
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_customer_status ON orders(user_id, status);
CREATE INDEX idx_orders_customer_created_at ON orders(user_id, created_at DESC);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);
CREATE INDEX idx_orders_address_id ON orders(address_id);
CREATE INDEX idx_orders_payment_id ON orders(payment_id);
CREATE INDEX idx_orders_delivery_info_id ON orders(delivery_info_id);
CREATE INDEX idx_orders_pickup_info_id ON orders(pick_up_info_id);

-- Indexes for deliveryInfo table
CREATE INDEX idx_delivery_info_order_id ON deliveryInfo(order_id);
CREATE INDEX idx_delivery_info_address_id ON deliveryInfo(address_id);
CREATE INDEX idx_delivery_info_tracking_number ON deliveryInfo(tracking_number);
CREATE INDEX idx_delivery_info_estimated_delivery ON deliveryInfo(estimated_delivery_date);
CREATE INDEX idx_delivery_info_actual_delivery ON deliveryInfo(actual_delivery_date);

-- Indexes for pickUpInfo table
CREATE INDEX idx_pickup_info_order_id ON pickUpInfo(order_id);
CREATE INDEX idx_pickup_info_store_id ON pickUpInfo(store_id);
CREATE INDEX idx_pickup_info_pickup_code ON pickUpInfo(pickup_code);
CREATE INDEX idx_pickup_info_pickup_date ON pickUpInfo(pickup_date);
CREATE INDEX idx_pickup_info_available_until ON pickUpInfo(available_until);


COMMENT ON TABLE orders IS 'Main table for customer orders';
COMMENT ON COLUMN orders.id IS 'Unique identifier for each order (UUID format)';
COMMENT ON COLUMN orders.user_id IS 'Client identifier (UUID format)';
COMMENT ON COLUMN orders.status IS 'Current status of the order';
COMMENT ON COLUMN orders.payment_id IS 'Payment identifier (no foreign key constraint)';
COMMENT ON COLUMN orders.delivery_info_id IS 'Reference to delivery information';
COMMENT ON COLUMN orders.pick_up_info_id IS 'Reference to pickup information';

COMMENT ON TABLE deliveryInfo IS 'Delivery information for orders with delivery method';
COMMENT ON COLUMN deliveryInfo.tracking_number IS 'Tracking number for delivery';
COMMENT ON COLUMN deliveryInfo.delivery_attempt IS 'Number of delivery attempts made';

COMMENT ON TABLE pickUpInfo IS 'Pickup information for orders with store pickup method';
COMMENT ON COLUMN pickUpInfo.pickup_code IS 'Code to pickup the order at the store';
COMMENT ON COLUMN pickUpInfo.available_until IS 'Date until the order is available for pickup';

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