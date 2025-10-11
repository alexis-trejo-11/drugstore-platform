-- Demo users for orders
INSERT INTO users (id, name, email, phone_number, created_at, updated_at, deleted_at, role, status)
VALUES
('1111-1111-1111-1111', 'John Doe', 'john.doe@example.com', '+1234567890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'CUSTOMER', 'ACTIVE'),
('2222-2222-2222-2222', 'Jane Smith', 'jane.smith@example.com', '+1234567891', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'CUSTOMER', 'ACTIVE'),
('3333-3333-3333-3333', 'Alice Johnson', 'alice.johnson@example.com', '+1234567892', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'CUSTOMER', 'ACTIVE'),
('4444-4444-4444-4444', 'Bob Brown', 'bob.brown@example.com', '+1234567893', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'CUSTOMER', 'ACTIVE'),
('5555-5555-5555-5555', 'Carol White', 'carol.white@example.com', '+1234567894', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'CUSTOMER', 'ACTIVE');


-- Insert demo data into addresses table (updated schema)
INSERT INTO addresses (id, country, city, state, neighborhood, zip_code, street, building_type, inner_number, outer_number, additional_info, user_id, is_default, created_at, updated_at)
VALUES
('1111-1111-1111-1111', 'USA', 'Springfield', 'Illinois', 'Downtown', '62701', '123 Main St', 'House', NULL, '123', 'Near the park', '1111-1111-1111-1111', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('2222-2222-2222-2222', 'USA', 'Metropolis', 'New York', 'Midtown', '10001', '456 Oak Ave', 'Apartment', '2B', '456', 'Apt 2B', '2222-2222-2222-2222', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('3333-3333-3333-3333', 'USA', 'Gotham', 'New Jersey', 'Financial District', '07001', '789 Pine Rd', 'Office', '300', '789', 'Suite 300', '3333-3333-3333-3333', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('4444-4444-4444-4444', 'USA', 'Star City', 'California', 'Industrial Zone', '90001', '101 Maple Dr', 'Office', NULL, '101', 'Office', '4444-4444-4444-4444', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('5555-5555-5555-5555', 'USA', 'Central City', 'Kansas', 'Logistics Park', '66002', '202 Birch Blvd', 'Warehouse', NULL, '202', 'Warehouse', '5555-5555-5555-5555', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('6666-6666-6666-6666', 'USA', 'Smallville', 'Kansas', 'Rural Area', '66003', '77 Elm St', 'House', NULL, '77', 'Farmhouse', '1111-1111-1111-1111', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('7777-7777-7777-7777', 'USA', 'Coast City', 'California', 'Harbor', '90002', '88 Cedar Ave', 'Apartment', 'PH', '88', 'Penthouse', '2222-2222-2222-2222', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert demo data into orders table referencing addresses
INSERT INTO orders (id, user_id, address_id, currency, delivery_method, status, shipping_cost, tax_amount, delivery_tracking_number, delivery_attempt, days_since_ready_for_pickup, payment_id, created_at, updated_at, estimated_delivery_date, notes)
VALUES
('aaaa-aaaa-aaaa-aaaa', '1111-1111-1111-1111', '1111-1111-1111-1111', 'MXN', 'standard_delivery', 'delivered', 5.99, 2.50, 'TRK-001-2024', 1, 0, 'pay-001', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 'Leave at front door'),
('bbbb-bbbb-bbbb-bbbb', '2222-2222-2222-2222', '2222-2222-2222-2222', 'MXN', 'store_pickup', 'preparing', 0.00, 1.80, NULL, 0, 2, 'pay-002', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1 day', 'Call on arrival'),
('cccc-cccc-cccc-cccc', '3333-3333-3333-3333', '3333-3333-3333-3333', 'MXN', 'express_delivery', 'out_for_delivery', 15.99, 4.20, 'TRK-002-2024', 0, 0, 'pay-003', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1 day', 'Fragile items'),
('dddd-dddd-dddd-dddd', '4444-4444-4444-4444', '4444-4444-4444-4444', 'MXN', 'standard_delivery', 'delivered', 3.99, 1.25, 'TRK-003-2024', 2, 0, 'pay-004', CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 'Deliver to office'),
('eeee-eeee-eeee-eeee', '5555-5555-5555-5555', '5555-5555-5555-5555', 'MXN', 'standard_delivery', 'cancelled', 5.99, 3.15, NULL, 0, 0, NULL, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP + INTERVAL '2 days', 'Order cancelled by customer');

-- Insert demo data into order_items table referencing orders
INSERT INTO order_items (order_id, product_id, product_name, subtotal, quantity, currency, is_prescription_required)
VALUES
-- Order 1
('aaaa-aaaa-aaaa-aaaa', 'prod-001', 'Paracetamol 500mg', 100.00, 2, 'MXN', FALSE),
('aaaa-aaaa-aaaa-aaaa', 'prod-002', 'Alcohol Gel 250ml', 170.00, 1, 'MXN', FALSE),
('aaaa-aaaa-aaaa-aaaa', 'prod-003', 'Vitamin C 1000mg', 340.00, 1, 'MXN', FALSE),
-- Order 2
('bbbb-bbbb-bbbb-bbbb', 'prod-004', 'Ibuprofen 400mg', 145.00, 1, 'MXN', FALSE),
('bbbb-bbbb-bbbb-bbbb', 'prod-005', 'KN95 Mask', 155.00, 2, 'MXN', FALSE),
-- Order 3
('cccc-cccc-cccc-cccc', 'prod-006', 'Children Cough Syrup', 258.00, 1, 'MXN', TRUE),
('cccc-cccc-cccc-cccc', 'prod-007', 'Digital Thermometer', 360.00, 1, 'MXN', FALSE),
('cccc-cccc-cccc-cccc', 'prod-008', 'Sterile Gauze', 180.00, 2, 'MXN', FALSE),
-- Order 4
('dddd-dddd-dddd-dddd', 'prod-009', 'Antiseptic Cream', 124.0, 1, 'MXN', FALSE),
('dddd-dddd-dddd-dddd', 'prod-010', 'Adhesive Bandages', 9.00, 2, 'MXN', FALSE),
-- Order 5
('eeee-eeee-eeee-eeee', 'prod-011', 'Throat Lozenges', 176.00, 1, 'MXN', FALSE),
('eeee-eeee-eeee-eeee', 'prod-012', 'Saline Solution', 190.00, 2, 'MXN', FALSE),
('eeee-eeee-eeee-eeee', 'prod-013', 'Latex Gloves', 190.00, 1, 'MXN', FALSE);
