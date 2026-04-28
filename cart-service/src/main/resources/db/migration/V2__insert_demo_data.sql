-- Insert demo products
INSERT INTO products (id, name, description, unit_price, discount_per_unit, is_available, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'Aspirin 100mg', 'Pain relief medication - 100mg tablets', 12.50, 1.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440002', 'Ibuprofen 200mg', 'Anti-inflammatory medication - 200mg tablets', 15.75, 0.50, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440003', 'Vitamin C 1000mg', 'Immune system support - 1000mg capsules', 22.99, 2.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440004', 'Paracetamol 500mg', 'Fever reducer and pain reliever - 500mg tablets', 8.25, 0.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440005', 'Cough Syrup 120ml', 'Cough suppressant syrup - 120ml bottle', 18.50, 0.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440006', 'Multivitamin Complex', 'Daily multivitamin supplement - 30 tablets', 35.00, 5.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440007', 'Band-Aid Strips', 'Adhesive bandages - pack of 20', 6.99, 0.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440008', 'Antiseptic Cream', 'Topical antibiotic cream - 30g tube', 14.25, 0.75, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440009', 'Digital Thermometer', 'Electronic digital thermometer', 45.00, 10.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440010', 'Hand Sanitizer 250ml', 'Alcohol-based hand sanitizer - 250ml bottle', 9.75, 0.00, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert demo carts
INSERT INTO carts (id, customer_id, created_at, updated_at) VALUES
('cart-001', 'customer-john-doe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('cart-002', 'customer-jane-smith', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('cart-003', 'customer-bob-johnson', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert demo cart items (removed per-item discount - now at product level)
INSERT INTO cart_items (id, cart_id, product_id, quantity, created_at, updated_at) VALUES
-- John Doe's cart items
('cartitem-001', 'cart-001', '550e8400-e29b-41d4-a716-446655440001', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('cartitem-002', 'cart-001', '550e8400-e29b-41d4-a716-446655440003', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('cartitem-003', 'cart-001', '550e8400-e29b-41d4-a716-446655440007', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Jane Smith's cart items
('cartitem-004', 'cart-002', '550e8400-e29b-41d4-a716-446655440002', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('cartitem-005', 'cart-002', '550e8400-e29b-41d4-a716-446655440004', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('cartitem-006', 'cart-002', '550e8400-e29b-41d4-a716-446655440006', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('cartitem-007', 'cart-002', '550e8400-e29b-41d4-a716-446655440009', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Bob Johnson's cart items
('cartitem-008', 'cart-003', '550e8400-e29b-41d4-a716-446655440005', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('cartitem-009', 'cart-003', '550e8400-e29b-41d4-a716-446655440008', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert demo afterwards items (items moved to wishlist/saved for later)
INSERT INTO afterwards (id, cart_id, product_id, quantity, added_at, created_at, updated_at) VALUES
-- John Doe's afterwards items
('afterwards-001', 'cart-001', '550e8400-e29b-41d4-a716-446655440002', 1, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP),
('afterwards-002', 'cart-001', '550e8400-e29b-41d4-a716-446655440009', 1, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),

-- Jane Smith's afterwards items
('afterwards-003', 'cart-002', '550e8400-e29b-41d4-a716-446655440001', 2, CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP),

-- Bob Johnson's afterwards items
('afterwards-004', 'cart-003', '550e8400-e29b-41d4-a716-446655440006', 1, CURRENT_TIMESTAMP - INTERVAL '5 hours', CURRENT_TIMESTAMP - INTERVAL '5 hours', CURRENT_TIMESTAMP),
('afterwards-005', 'cart-003', '550e8400-e29b-41d4-a716-446655440007', 2, CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP);

-- Add some additional demo data for testing purposes
INSERT INTO carts (id, customer_id, created_at, updated_at) VALUES
('cart-004', 'customer-alice-williams', CURRENT_TIMESTAMP - INTERVAL '1 week', CURRENT_TIMESTAMP),
('cart-005', 'customer-david-brown', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP);

-- Empty carts for testing edge cases
INSERT INTO carts (id, customer_id, created_at, updated_at) VALUES
('cart-006', 'customer-empty-cart-1', CURRENT_TIMESTAMP - INTERVAL '2 weeks', CURRENT_TIMESTAMP),
('cart-007', 'customer-empty-cart-2', CURRENT_TIMESTAMP - INTERVAL '1 month', CURRENT_TIMESTAMP);

-- Add cart items for additional customers
INSERT INTO cart_items (id, cart_id, product_id, quantity, created_at, updated_at) VALUES
-- Alice Williams' cart items (large cart)
('cartitem-010', 'cart-004', '550e8400-e29b-41d4-a716-446655440001', 5, CURRENT_TIMESTAMP - INTERVAL '1 week', CURRENT_TIMESTAMP),
('cartitem-011', 'cart-004', '550e8400-e29b-41d4-a716-446655440002', 3, CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP),
('cartitem-012', 'cart-004', '550e8400-e29b-41d4-a716-446655440003', 2, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP),
('cartitem-013', 'cart-004', '550e8400-e29b-41d4-a716-446655440004', 4, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP),
('cartitem-014', 'cart-004', '550e8400-e29b-41d4-a716-446655440005', 1, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP),

-- David Brown's cart items (single item)
('cartitem-015', 'cart-005', '550e8400-e29b-41d4-a716-446655440009', 1, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP);

-- Comments for demo data context
-- John Doe: Active shopper with mixed cart and afterwards items
-- Jane Smith: Premium customer with discounts and high-value items
-- Bob Johnson: Budget-conscious shopper with small quantities
-- Alice Williams: Bulk buyer with multiple items and discounts
-- David Brown: Single expensive item purchase
-- Empty cart customers: Edge case testing
