-- Create addresses table and link to orders table
CREATE TABLE addresses (
    id VARCHAR(36) PRIMARY KEY,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    additional_info VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Migrate existing address data from orders to addresses table
ALTER TABLE orders ADD COLUMN address_id VARCHAR(36);

ALTER TABLE orders
ADD CONSTRAINT fk_order_address
FOREIGN KEY (address_id) REFERENCES addresses(id);

-- Indexes for new table and foreign key
CREATE INDEX idx_orders_address_id ON orders(address_id);
CREATE INDEX idx_addresses_city ON addresses(city);
CREATE INDEX idx_addresses_state ON addresses(state);