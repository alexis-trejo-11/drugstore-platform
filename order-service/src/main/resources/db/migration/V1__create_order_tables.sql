CREATE TABLE orders (
    order_id VARCHAR(36) PRIMARY KEY,
    customer_id VARCHAR(36) NOT NULL,
    total_amount NUMERIC(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'MXN',
    delivery_method VARCHAR(20) NOT NULL,
    street VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    additional_info VARCHAR(500),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    estimated_delivery_date TIMESTAMP,
    notes VARCHAR(500),

    -- Constraints
    CONSTRAINT chk_delivery_method CHECK (delivery_method IN ('HOME_DELIVERY', 'STORE_PICKUP', 'EXPRESS_DELIVERY', 'STANDARD_DELIVERY')),
    CONSTRAINT chk_order_status CHECK (status IN ('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'RETURNED')),
    CONSTRAINT chk_currency CHECK (currency IN ('MXN', 'USD', 'EUR')),
    CONSTRAINT chk_total_amount_positive CHECK (total_amount >= 0)
);

-- Indexes
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_customer_status ON orders(customer_id, status);
CREATE INDEX idx_orders_customer_created_at ON orders(customer_id, created_at DESC);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);

COMMENT ON TABLE orders IS 'Main table for customer orders';
COMMENT ON COLUMN orders.order_id IS 'Unique identifier for each order (UUID format)';
COMMENT ON COLUMN orders.customer_id IS 'Client identifier (UUID format)';
COMMENT ON COLUMN orders.status IS 'Current status of the order';

CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL,
    quantity INTEGER NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'MXN',
    subtotal NUMERIC(10,2) NOT NULL,
    prescription_required BOOLEAN DEFAULT FALSE,

    -- Foreign key
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_unit_price_positive CHECK (unit_price >= 0),
    CONSTRAINT chk_quantity_positive CHECK (quantity > 0),
    CONSTRAINT chk_subtotal_positive CHECK (subtotal >= 0)
);


-- Indexes
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);

COMMENT ON TABLE order_items IS 'Items/productos de cada orden';
COMMENT ON COLUMN order_items.prescription_required IS 'Indica si el producto requiere receta médica';


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


-- VIew to summarize orders
CREATE OR REPLACE VIEW v_order_summary AS
SELECT
    o.order_id,
    o.customer_id,
    o.total_amount,
    o.currency,
    o.status,
    o.created_at,
    o.estimated_delivery_date,
    COUNT(oi.id) as total_items,
    SUM(oi.quantity) as total_quantity
FROM orders o
LEFT JOIN order_items oi ON o.order_id = oi.order_id
GROUP BY o.order_id, o.customer_id, o.total_amount, o.currency, o.status, o.created_at, o.estimated_delivery_date;

-- View for detailed order information
CREATE OR REPLACE VIEW v_order_details AS
SELECT
    o.order_id,
    o.customer_id,
    o.total_amount,
    o.currency,
    o.delivery_method,
    o.status,
    o.created_at,
    o.updated_at,
    o.estimated_delivery_date,
    oi.product_id,
    oi.product_name,
    oi.unit_price,
    oi.quantity,
    oi.subtotal,
    oi.prescription_required
FROM orders o
INNER JOIN order_items oi ON o.order_id = oi.order_id;



CREATE OR REPLACE FUNCTION get_customer_order_count(p_customer_id VARCHAR)
RETURNS BIGINT AS $$
BEGIN
    RETURN (SELECT COUNT(*) FROM orders WHERE customer_id = p_customer_id);
END;
$$ LANGUAGE plpgsql;

-- Función para obtener el gasto total de un cliente
CREATE OR REPLACE FUNCTION get_customer_total_spent(p_customer_id VARCHAR)
RETURNS NUMERIC AS $$
BEGIN
    RETURN (
        SELECT COALESCE(SUM(total_amount), 0)
        FROM orders
        WHERE customer_id = p_customer_id
        AND status IN ('DELIVERED', 'SHIPPED')
    );
END;
$$ LANGUAGE plpgsql;


-- Index for queries filtering by customer_id and date range
CREATE INDEX idx_orders_customer_date_range ON orders(customer_id, created_at DESC)
WHERE created_at IS NOT NULL;

-- Partial Index for active orders (not delivered or cancelled)
CREATE INDEX idx_orders_active ON orders(customer_id, created_at DESC)
WHERE status NOT IN ('DELIVERED', 'CANCELLED');

-- Index to optimize recent orders retrieval
CREATE INDEX idx_orders_recent ON orders(customer_id, created_at DESC)
INCLUDE (order_id, status, total_amount);

-- Demo Data
INSERT INTO orders (
    order_id, customer_id, total_amount, currency, delivery_method, street, city, state, postal_code, country, status, created_at, estimated_delivery_date, notes
) VALUES (
    '11111111-1111-1111-1111-111111111111',
    '22222222-2222-2222-2222-222222222222',
    299.50,
    'MXN',
    'HOME_DELIVERY',
    'Av. Insurgentes Sur 1234',
    'Ciudad de México',
    'CDMX',
    '03100',
    'México',
    'PENDING',
    NOW(),
    NOW() + INTERVAL '2 days',
    'Entregar en horario matutino'
);

INSERT INTO order_items (
    order_id, product_id, product_name, unit_price, quantity, subtotal, currency, prescription_required
) VALUES
(
    '11111111-1111-1111-1111-111111111111',
    '33333333-3333-3333-3333-333333333333',
    'Aspirina 500mg x 20 tabletas',
    45.50,
    2,
    91.00,
    'MXN',
    false
),
(
    '11111111-1111-1111-1111-111111111111',
    '44444444-4444-4444-4444-444444444444',
    'Paracetamol 500mg x 30 cápsulas',
    104.25,
    2,
    208.50,
    'MXN',
    false
);