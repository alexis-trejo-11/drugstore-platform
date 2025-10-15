-- Demo users for orders
INSERT INTO users (id, name, email, phone_number, created_at, updated_at, deleted_at, role, status)
VALUES
('11111111-1111-1111-1111-111111111111', 'John Doe', 'john.doe@example.com', '+1234567890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'CUSTOMER', 'ACTIVE'),
('22222222-2222-2222-2222-222222222222', 'Jane Smith', 'jane.smith@example.com', '+1234567891', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'CUSTOMER', 'ACTIVE'),
('33333333-3333-3333-3333-333333333333', 'Alice Johnson', 'alice.johnson@example.com', '+1234567892', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'CUSTOMER', 'ACTIVE'),
('44444444-4444-4444-4444-444444444444', 'Bob Brown', 'bob.brown@example.com', '+1234567893', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'CUSTOMER', 'ACTIVE'),
('55555555-5555-5555-5555-555555555555', 'Carol White', 'carol.white@example.com', '+1234567894', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'CUSTOMER', 'ACTIVE');


-- Insert demo data into addresses table (updated schema)
INSERT INTO addresses (id, country, city, state, neighborhood, zip_code, street, building_type, inner_number, outer_number, additional_info, user_id, is_default, created_at, updated_at)
VALUES
('a1111111-1111-1111-1111-111111111111', 'USA', 'Springfield', 'Illinois', 'Downtown', '62701', '123 Main St', 'House', NULL, '123', 'Near the park', '11111111-1111-1111-1111-111111111111', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('a2222222-2222-2222-2222-222222222222', 'USA', 'Metropolis', 'New York', 'Midtown', '10001', '456 Oak Ave', 'Apartment', '2B', '456', 'Apt 2B', '22222222-2222-2222-2222-222222222222', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('a3333333-3333-3333-3333-333333333333', 'USA', 'Gotham', 'New Jersey', 'Financial District', '07001', '789 Pine Rd', 'Office', '300', '789', 'Suite 300', '33333333-3333-3333-3333-333333333333', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('a4444444-4444-4444-4444-444444444444', 'USA', 'Star City', 'California', 'Industrial Zone', '90001', '101 Maple Dr', 'Office', NULL, '101', 'Office', '44444444-4444-4444-4444-444444444444', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('a5555555-5555-5555-5555-555555555555', 'USA', 'Central City', 'Kansas', 'Logistics Park', '66002', '202 Birch Blvd', 'Warehouse', NULL, '202', 'Warehouse', '55555555-5555-5555-5555-555555555555', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('a6666666-6666-6666-6666-666666666666', 'USA', 'Smallville', 'Kansas', 'Rural Area', '66003', '77 Elm St', 'House', NULL, '77', 'Farmhouse', '11111111-1111-1111-1111-111111111111', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('a7777777-7777-7777-7777-777777777777', 'USA', 'Coast City', 'California', 'Harbor', '90002', '88 Cedar Ave', 'Apartment', 'PH', '88', 'Penthouse', '22222222-2222-2222-2222-222222222222', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert demo data into orders table with new structure
INSERT INTO orders (id, user_id, address_id, currency, delivery_method, status, service_fee, tax_fee, payment_id, delivery_info_id, pick_up_info_id, notes, created_at, updated_at)
VALUES
-- Order 1: Standard delivery (delivered)
('o1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'a1111111-1111-1111-1111-111111111111', 'MXN', 'standard_delivery', 'delivered', 5.99, 2.50, 'pay-111111111111111111111111111111111111', 'd1111111-1111-1111-1111-111111111111', NULL, 'Leave at front door', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),

-- Order 2: Store pickup (ready for pickup)
('o2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', NULL, 'MXN', 'store_pickup', 'ready_for_pickup', 0.00, 1.80, 'pay-222222222222222222222222222222222222', NULL, 'p1111111-1111-1111-1111-111111111111', 'Call on arrival', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP),

-- Order 3: Express delivery (out for delivery)
('o3333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', 'a3333333-3333-3333-3333-333333333333', 'MXN', 'express_delivery', 'out_for_delivery', 15.99, 4.20, 'pay-333333333333333333333333333333333333', 'd2222222-2222-2222-2222-222222222222', NULL, 'Fragile items', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),

-- Order 4: Standard delivery (delivered)
('o4444444-4444-4444-4444-444444444444', '44444444-4444-4444-4444-444444444444', 'a4444444-4444-4444-4444-444444444444', 'MXN', 'standard_delivery', 'delivered', 3.99, 1.25, 'pay-444444444444444444444444444444444444', 'd3333333-3333-3333-3333-333333333333', NULL, 'Deliver to office', CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),

-- Order 5: Store pickup (picked up)
('o5555555-5555-5555-5555-555555555555', '55555555-5555-5555-5555-555555555555', NULL, 'MXN', 'store_pickup', 'picked_up', 0.00, 3.15, 'pay-555555555555555555555555555555555555', NULL, 'p2222222-2222-2222-2222-222222222222', 'Order picked up successfully', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '1 day');

-- Insert delivery information for delivery orders
INSERT INTO deliveryInfo (id, tracking_number, delivery_attempt, estimated_delivery_date, actual_delivery_date, delivery_cost, order_id, address_id, created_at, updated_at)
VALUES
-- Delivery info for Order 1 (delivered)
('d1111111-1111-1111-1111-111111111111', 'TRK-001-2024-DRUG', 1, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 5.99, 'o1111111-1111-1111-1111-111111111111', 'a1111111-1111-1111-1111-111111111111', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),

-- Delivery info for Order 3 (out for delivery)
('d2222222-2222-2222-2222-222222222222', 'TRK-002-2024-DRUG', 0, CURRENT_TIMESTAMP + INTERVAL '1 day', NULL, 15.99, 'o3333333-3333-3333-3333-333333333333', 'a3333333-3333-3333-3333-333333333333', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),

-- Delivery info for Order 4 (delivered with multiple attempts)
('d3333333-3333-3333-3333-333333333333', 'TRK-003-2024-DRUG', 2, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 3.99, 'o4444444-4444-4444-4444-444444444444', 'a4444444-4444-4444-4444-444444444444', CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '3 days');

-- Insert pickup information for store pickup orders
INSERT INTO pickUpInfo (id, pickup_code, pickup_date, store_id, store_name, store_address, order_id, picked_up_at, available_until, created_at, updated_at)
VALUES
-- Pickup info for Order 2 (ready for pickup)
('p1111111-1111-1111-1111-111111111111', 'PICKUP-2024-001', CURRENT_TIMESTAMP - INTERVAL '1 day', 'store-11111111-1111-1111-1111-111111111111', 'Drugstore Central - Metropolis', '100 Main Street, Metropolis, NY 10001', 'o2222222-2222-2222-2222-222222222222', NULL, CURRENT_TIMESTAMP + INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP),

-- Pickup info for Order 5 (already picked up)
('p2222222-2222-2222-2222-222222222222', 'PICKUP-2024-002', CURRENT_TIMESTAMP - INTERVAL '2 days', 'store-22222222-2222-2222-2222-222222222222', 'Drugstore Plaza - Central City', '500 Commerce Blvd, Central City, KS 66002', 'o5555555-5555-5555-5555-555555555555', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '1 day');

-- Insert demo data into order_items table referencing orders
INSERT INTO order_items (order_id, product_id, product_name, subtotal, quantity, currency, is_prescription_required)
VALUES
-- Order 1
('o1111111-1111-1111-1111-111111111111', 'prod-111111111111111111111111111111111111', 'Paracetamol 500mg', 100.00, 2, 'MXN', FALSE),
('o1111111-1111-1111-1111-111111111111', 'prod-222222222222222222222222222222222222', 'Alcohol Gel 250ml', 170.00, 1, 'MXN', FALSE),
('o1111111-1111-1111-1111-111111111111', 'prod-333333333333333333333333333333333333', 'Vitamin C 1000mg', 340.00, 1, 'MXN', FALSE),
-- Order 2
('o2222222-2222-2222-2222-222222222222', 'prod-444444444444444444444444444444444444', 'Ibuprofen 400mg', 145.00, 1, 'MXN', FALSE),
('o2222222-2222-2222-2222-222222222222', 'prod-555555555555555555555555555555555555', 'KN95 Mask', 155.00, 2, 'MXN', FALSE),
-- Order 3
('o3333333-3333-3333-3333-333333333333', 'prod-666666666666666666666666666666666666', 'Children Cough Syrup', 258.00, 1, 'MXN', TRUE),
('o3333333-3333-3333-3333-333333333333', 'prod-777777777777777777777777777777777777', 'Digital Thermometer', 360.00, 1, 'MXN', FALSE),
('o3333333-3333-3333-3333-333333333333', 'prod-888888888888888888888888888888888888', 'Sterile Gauze', 180.00, 2, 'MXN', FALSE),
-- Order 4
('o4444444-4444-4444-4444-444444444444', 'prod-999999999999999999999999999999999999', 'Antiseptic Cream', 124.00, 1, 'MXN', FALSE),
('o4444444-4444-4444-4444-444444444444', 'prod-aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 'Adhesive Bandages', 90.00, 2, 'MXN', FALSE),
-- Order 5
('o5555555-5555-5555-5555-555555555555', 'prod-bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 'Throat Lozenges', 176.00, 1, 'MXN', FALSE),
('o5555555-5555-5555-5555-555555555555', 'prod-cccccccccccccccccccccccccccccccccccc', 'Saline Solution', 190.00, 2, 'MXN', FALSE),
('o5555555-5555-5555-5555-555555555555', 'prod-dddddddddddddddddddddddddddddddddddd', 'Latex Gloves', 190.00, 1, 'MXN', FALSE);
