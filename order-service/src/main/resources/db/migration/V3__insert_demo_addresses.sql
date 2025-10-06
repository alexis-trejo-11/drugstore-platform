-- Insert demo data into addresses table for testing
INSERT INTO addresses (id, street, city, state, postal_code, country, additional_info, created_at, updated_at)
VALUES
('1111-1111-1111-1111', '123 Main St', 'Springfield', 'Illinois', '62701', 'USA', 'Near the park', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('2222-2222-2222-2222', '456 Oak Ave', 'Metropolis', 'New York', '10001', 'USA', 'Apt 2B', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('3333-3333-3333-3333', '789 Pine Rd', 'Gotham', 'New Jersey', '07001', 'USA', 'Suite 300', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('4444-4444-4444-4444', '101 Maple Dr', 'Star City', 'California', '90001', 'USA', 'Office', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('5555-5555-5555-5555', '202 Birch Blvd', 'Central City', 'Kansas', '66002', 'USA', 'Warehouse', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('6666-6666-6666-6666', '77 Elm St', 'Smallville', 'Kansas', '66003', 'USA', 'Farmhouse', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('7777-7777-7777-7777', '88 Cedar Ave', 'Coast City', 'California', '90002', 'USA', 'Penthouse', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert demo data into orders table referencing addresses
INSERT INTO orders (id, customer_id, currency, delivery_method, status, created_at, updated_at, estimated_delivery_date, notes)
VALUES
('aaaa-aaaa-aaaa-aaaa', '1111-1111-1111-1111', 'USD', 'HOME_DELIVERY', 'DELIVERED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '2 days', 'Leave at front door'),
('bbbb-bbbb-bbbb-bbbb', '2222-2222-2222-2222', 'USD', 'STORE_PICKUP', 'PROCESSING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '3 days', 'Call on arrival'),
('cccc-cccc-cccc-cccc', '3333-3333-3333-3333', 'USD', 'EXPRESS_DELIVERY', 'SHIPPED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1 day', 'Fragile items'),
('dddd-dddd-dddd-dddd', '4444-4444-4444-4444', 'USD', 'STANDARD_DELIVERY', 'DELIVERED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '4 days', 'Deliver to office'),
('eeee-eeee-eeee-eeee', '5555-5555-5555-5555', 'USD', 'HOME_DELIVERY', 'CANCELLED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '2 days', 'Order cancelled by customer');

-- Insert demo data into order_items table referencing orders
INSERT INTO order_items (order_id, product_id, product_name, unit_price, quantity, currency, prescription_required)
VALUES
-- Order 1
('aaaa-aaaa-aaaa-aaaa', 'prod-001', 'Paracetamol 500mg', 5.00, 2, 'USD', FALSE),
('aaaa-aaaa-aaaa-aaaa', 'prod-002', 'Alcohol Gel 250ml', 8.50, 1, 'USD', FALSE),
('aaaa-aaaa-aaaa-aaaa', 'prod-003', 'Vitamin C 1000mg', 17.00, 1, 'USD', FALSE),
-- Order 2
('bbbb-bbbb-bbbb-bbbb', 'prod-004', 'Ibuprofen 400mg', 7.25, 1, 'USD', FALSE),
('bbbb-bbbb-bbbb-bbbb', 'prod-005', 'KN95 Mask', 7.75, 2, 'USD', FALSE),
-- Order 3
('cccc-cccc-cccc-cccc', 'prod-006', 'Children Cough Syrup', 12.90, 1, 'USD', TRUE),
('cccc-cccc-cccc-cccc', 'prod-007', 'Digital Thermometer', 18.00, 1, 'USD', FALSE),
('cccc-cccc-cccc-cccc', 'prod-008', 'Sterile Gauze', 9.00, 2, 'USD', FALSE),
-- Order 4
('dddd-dddd-dddd-dddd', 'prod-009', 'Antiseptic Cream', 6.20, 1, 'USD', FALSE),
('dddd-dddd-dddd-dddd', 'prod-010', 'Adhesive Bandages', 4.50, 2, 'USD', FALSE),
-- Order 5
('eeee-eeee-eeee-eeee', 'prod-011', 'Throat Lozenges', 8.80, 1, 'USD', FALSE),
('eeee-eeee-eeee-eeee', 'prod-012', 'Saline Solution', 9.50, 2, 'USD', FALSE),
('eeee-eeee-eeee-eeee', 'prod-013', 'Latex Gloves', 9.50, 1, 'USD', FALSE);
