-- Create Shipping Status Enum
CREATE TYPE shipping_status AS ENUM ('PENDING', 'SHIPPED', 'DELIVERED', 'CANCELLED');

-- Create Shipping Data Table
CREATE TABLE shipping_data (
    id SERIAL PRIMARY KEY,
    recipient_name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    service_type VARCHAR(100),
    shipping_cost DECIMAL(10, 2) NOT NULL,
    shipping_status shipping_status NOT NULL,
    tracking_number VARCHAR(50)
);

-- Create Orders Table
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    payment_id BIGINT,
    address_id BIGINT,
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_order_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    delivery_tries INT DEFAULT 0,
    shipping_data_id BIGINT,
    CONSTRAINT fk_shipping_data
        FOREIGN KEY (shipping_data_id)
        REFERENCES shipping_data(id)
        ON DELETE CASCADE
);

-- Create Order Items Table
CREATE TABLE order_items (
    id SERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_unit_price DECIMAL(10, 2) NOT NULL,
    product_quantity INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE
);


INSERT INTO shipping_data (recipient_name, address, city, state, postal_code, country, phone_number, service_type, shipping_cost, shipping_status, tracking_number)
VALUES
('John Doe', '123 Main St', 'New York', 'NY', '10001', 'USA', '555-1234', 'Standard', 5.99, 'PENDING', 'TRACK123456'),
('Jane Smith', '456 Elm St', 'Los Angeles', 'CA', '90001', 'USA', '555-5678', 'Express', 9.99, 'PENDING', 'TRACK567890');


INSERT INTO orders (client_id, payment_id, address_id, order_date, last_order_update, status, delivery_tries, shipping_data_id)
VALUES
(1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'PROCESSING', 0, 1),
(2, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'PROCESSING', 0, 2);

INSERT INTO order_items (order_id, product_id, product_name, product_unit_price, product_quantity, created_at)
VALUES
(1, 1, 'Extra Strength Tylenol', 9.99, 2, CURRENT_TIMESTAMP),
(1, 2, 'Neutrogena Hydro Boost', 15.99, 1, CURRENT_TIMESTAMP),
(1, 3, 'Pampers Swaddlers', 24.99, 3, CURRENT_TIMESTAMP),
(2, 4, 'Robitussin Cough Syrup', 8.99, 1, CURRENT_TIMESTAMP),
(2, 5, 'Centrum Adult Multivitamin', 12.99, 2, CURRENT_TIMESTAMP),
(2, 6, 'Band-Aid Flexible Fabric Adhesive Bandages', 4.99, 5, CURRENT_TIMESTAMP);
