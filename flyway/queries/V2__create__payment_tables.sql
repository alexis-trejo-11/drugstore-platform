CREATE TABLE cards (
    id SERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    card_number TEXT NOT NULL,
    cardholder_name VARCHAR(255) NOT NULL,
    expiration_date DATE NOT NULL,
    cvv TEXT NOT NULL,
    is_card_valid BOOLEAN NOT NULL,
    card_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
);


CREATE TABLE payments (
    id SERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    discount DECIMAL(10, 2) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    card_id BIGINT REFERENCES Card(id),
    sale_id BIGINT,
    order_id BIGINT,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE transaction_date (
    id SERIAL PRIMARY KEY,
    payment_id BIGINT NOT NULL REFERENCES payments(id),
    status VARCHAR(255) NOT NULL,
    transaction_date TIMESTAMP NOT NULL
);


INSERT INTO Card (client_id, card_number, cardholder_name, expiration_date, cvv, is_card_valid, card_type)
VALUES
(1, '1234567812345678', 'John Doe', '2025-12-31', '123', TRUE, 'VISA'),
(2, '8765432187654321', 'Jane Smith', '2024-10-31', '321', TRUE, 'MASTERCARD'),
(3, '1111222233334444', 'Alice Johnson', '2026-07-31', '456', TRUE, 'AMEX'),
(4, '5555666677778888', 'Bob Brown', '2024-05-15', '789', TRUE, 'DISCOVER'),
(5, '9999000011112222', 'Charlie Davis', '2027-03-20', '012', TRUE, 'VISA'),
(6, '3333444455556666', 'Dana Evans', '2025-11-30', '345', TRUE, 'MASTERCARD');


INSERT INTO payments (client_id, payment_method, subtotal, discount, total, payment_date, card_id, sale_id, order_id, status)
VALUES
(1, 'CARD', 100.00, 10.00, 90.00, CURRENT_TIMESTAMP, 1, 1, 1, 'SUCCESS'),
(2, 'CASH', 50.00, 0.00, 50.00, CURRENT_TIMESTAMP, NULL, 2, 2, 'PENDING'),
(3, 'CARD', 200.00, 20.00, 180.00, CURRENT_TIMESTAMP, 3, 3, 3, 'SUCCESS'),
(4, 'CARD', 75.00, 5.00, 70.00, CURRENT_TIMESTAMP, 4, 4, 4, 'FAILURE'),
(5, 'CASH', 120.00, 10.00, 110.00, CURRENT_TIMESTAMP, NULL, 5, 5, 'PENDING'),
(6, 'CARD', 95.00, 15.00, 80.00, CURRENT_TIMESTAMP, 6, 6, 6, 'SUCCESS');

INSERT INTO transaction_date (payment_id, status, transaction_date)
VALUES
(1, 'COMPLETED', CURRENT_TIMESTAMP),
(2, 'PENDING', CURRENT_TIMESTAMP),
(3, 'COMPLETED', CURRENT_TIMESTAMP),
(4, 'FAILED', CURRENT_TIMESTAMP),
(5, 'PENDING', CURRENT_TIMESTAMP),
(6, 'COMPLETED', CURRENT_TIMESTAMP);
