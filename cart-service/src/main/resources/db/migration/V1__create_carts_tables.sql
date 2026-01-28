-- Create products table
CREATE TABLE products (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    unit_price DECIMAL(10,2) NOT NULL,
    discount_per_unit DECIMAL(10,2) DEFAULT 0.0,
    is_available BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create carts table
CREATE TABLE carts (
    id VARCHAR(36) PRIMARY KEY,
    customer_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create cart_items table
CREATE TABLE cart_items (
    id VARCHAR(36) PRIMARY KEY,
    cart_id VARCHAR(255) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create afterwards table
CREATE TABLE afterwards (
    id VARCHAR(36) PRIMARY KEY,
    cart_id VARCHAR(255) NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    added_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Add foreign key constraints
ALTER TABLE cart_items
    ADD CONSTRAINT fk_cart_items_cart_id
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE;

ALTER TABLE cart_items
    ADD CONSTRAINT fk_cart_items_product_id
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE;

ALTER TABLE afterwards
    ADD CONSTRAINT fk_afterwards_cart_id
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE;

-- Add check constraints
ALTER TABLE products
    ADD CONSTRAINT chk_products_unit_price_positive
    CHECK (unit_price >= 0);

ALTER TABLE cart_items
    ADD CONSTRAINT chk_cart_items_quantity_positive
    CHECK (quantity > 0);

ALTER TABLE afterwards
    ADD CONSTRAINT chk_afterwards_quantity_positive
    CHECK (quantity > 0);

-- Create indexes for performance
-- Products table indexes
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_is_available ON products(is_available);
CREATE INDEX idx_products_unit_price ON products(unit_price);
CREATE INDEX idx_products_created_at ON products(created_at);
CREATE INDEX idx_products_discount_per_unit ON products(discount_per_unit);

-- Carts table indexes
CREATE INDEX idx_carts_customer_id ON carts(customer_id);
CREATE INDEX idx_carts_created_at ON carts(created_at);
CREATE INDEX idx_carts_updated_at ON carts(updated_at);

-- Cart items table indexes
CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX idx_cart_items_product_id ON cart_items(product_id);
CREATE INDEX idx_cart_items_created_at ON cart_items(created_at);
CREATE INDEX idx_cart_items_cart_product ON cart_items(cart_id, product_id); -- Composite index for cart-product lookups

-- Afterwards table indexes
CREATE INDEX idx_afterwards_cart_id ON afterwards(cart_id);
CREATE INDEX idx_afterwards_product_id ON afterwards(product_id);
CREATE INDEX idx_afterwards_added_at ON afterwards(added_at);
CREATE INDEX idx_afterwards_created_at ON afterwards(created_at);
CREATE INDEX idx_afterwards_cart_product ON afterwards(cart_id, product_id); -- Composite index for cart-product lookups

-- Add unique constraints where appropriate
ALTER TABLE cart_items
    ADD CONSTRAINT uk_cart_items_cart_product
    UNIQUE (cart_id, product_id); -- Prevent duplicate products in the same cart

-- Comments for documentation
COMMENT ON TABLE products IS 'Products catalog table';
COMMENT ON TABLE carts IS 'Shopping carts table';
COMMENT ON TABLE cart_items IS 'Items currently in shopping carts';
COMMENT ON TABLE afterwards IS 'Items moved to afterwards/wishlist from carts';

COMMENT ON COLUMN products.id IS 'Unique product identifier (UUID)';
COMMENT ON COLUMN products.unit_price IS 'Price per unit in currency decimal format';
COMMENT ON COLUMN products.discount_per_unit IS 'Default discount per unit for the product (applies at product level)';
COMMENT ON COLUMN carts.customer_id IS 'Reference to customer who owns this cart';
COMMENT ON COLUMN cart_items.quantity IS 'Number of units of the product in cart';
COMMENT ON COLUMN afterwards.added_at IS 'When the item was moved to afterwards list';
