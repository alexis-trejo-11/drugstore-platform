CREATE TABLE digital_sales (
    id SERIAL PRIMARY KEY,
    sale_date TIMESTAMP,
    sale_total DECIMAL(10, 2),
    discount DECIMAL(10, 2),
    total DECIMAL(10, 2),
    client_id BIGINT,
    sale_status VARCHAR(20),
    order_id BIGINT,
    payment_id BIGINT
);

CREATE TABLE digital_sale_items (
    id SERIAL PRIMARY KEY,
    digital_sale_id BIGINT,
    product_id BIGINT,
    product_name VARCHAR(255),
    product_unit_price DECIMAL(10, 2),
    product_quantity INT,
    created_at TIMESTAMP,
    FOREIGN KEY (digital_sale_id) REFERENCES digital_sales(id) ON DELETE CASCADE
);

CREATE TYPE sale_status AS ENUM ('PENDING', 'COMPLETED', 'CANCELLED', 'REFUNDED');
ALTER TABLE digital_sales ALTER COLUMN sale_status TYPE sale_status USING sale_status::sale_status;

-- Insert into digital_sales
INSERT INTO digital_sales (sale_date, sale_total, discount, total, client_id, sale_status, order_id, payment_id) VALUES
(CURRENT_TIMESTAMP, 9.99, 0, 9.99, 1, 'COMPLETED', 1, 1),
(CURRENT_TIMESTAMP, 15.99, 0, 15.99, 2, 'COMPLETED', 2, 2),
(CURRENT_TIMESTAMP, 24.99, 0, 24.99, 3, 'COMPLETED', 3, 3),
(CURRENT_TIMESTAMP, 8.99, 0, 8.99, 4, 'COMPLETED', 4, 4),
(CURRENT_TIMESTAMP, 12.99, 0, 12.99, 5, 'COMPLETED', 5, 5),
(CURRENT_TIMESTAMP, 4.99, 0, 4.99, 6, 'COMPLETED', 6, 6),
(CURRENT_TIMESTAMP, 3.99, 0, 3.99, 7, 'COMPLETED', 7, 7),
(CURRENT_TIMESTAMP, 7.99, 0, 7.99, 8, 'COMPLETED', 8, 8),
(CURRENT_TIMESTAMP, 10.99, 0, 10.99, 9, 'COMPLETED', 9, 9),
(CURRENT_TIMESTAMP, 22.99, 0, 22.99, 10, 'COMPLETED', 10, 10);

-- Insert into digital_sale_items
INSERT INTO digital_sale_items (digital_sale_id, product_id, product_name, product_unit_price, product_quantity, created_at) VALUES
(1, 1, 'Extra Strength Tylenol', 9.99, 1, CURRENT_TIMESTAMP),
(2, 2, 'Neutrogena Hydro Boost', 15.99, 1, CURRENT_TIMESTAMP),
(3, 3, 'Pampers Swaddlers', 24.99, 1, CURRENT_TIMESTAMP),
(4, 4, 'Robitussin Cough Syrup', 8.99, 1, CURRENT_TIMESTAMP),
(5, 5, 'Centrum Adult Multivitamin', 12.99, 1, CURRENT_TIMESTAMP),
(6, 6, 'Band-Aid Flexible Fabric Adhesive Bandages', 4.99, 1, CURRENT_TIMESTAMP),
(7, 7, 'Colgate Total Whitening Toothpaste', 3.99, 1, CURRENT_TIMESTAMP),
(8, 8, 'Advil Liqui-Gels', 7.99, 1, CURRENT_TIMESTAMP),
(9, 9, 'Cetaphil Gentle Skin Cleanser', 10.99, 1, CURRENT_TIMESTAMP),
(10, 10, 'Huggies Little Snugglers', 22.99, 1, CURRENT_TIMESTAMP);
