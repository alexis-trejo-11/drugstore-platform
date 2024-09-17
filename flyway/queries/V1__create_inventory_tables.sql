CREATE TABLE inventory_items (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    batch_number VARCHAR(50),
    location VARCHAR(50),
    reorder_point INT,
    optimal_stock_level INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);


CREATE TABLE inventory_transactions (
    id BIGSERIAL PRIMARY KEY,
    inventory_item_id BIGINT NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    transaction_date TIMESTAMP,
    expiration_date TIMESTAMP,
    notes VARCHAR(200),
    supplier_id BIGINT,
    employee_id BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_inventory_item
        FOREIGN KEY (inventory_item_id)
        REFERENCES inventory_items(id)
        ON DELETE CASCADE
);

INSERT INTO inventory_items (product_id, quantity, batch_number, location, reorder_point, optimal_stock_level, created_at, updated_at)
VALUES
(1, 100, 'BATCH001', 'A1', 10, 200, NOW(), NOW()),
(1, 150, 'BATCH002', 'A2', 15, 250, NOW(), NOW()),
(2, 80, 'BATCH003', 'A3', 8, 180, NOW(), NOW()),
(4, 200, 'BATCH004', 'A4', 20, 300, NOW(), NOW()),
(5, 120, 'BATCH005', 'A5', 12, 220, NOW(), NOW()),
(6, 90, 'BATCH006', 'B1', 9, 190, NOW(), NOW()),
(7, 110, 'BATCH007', 'B2', 11, 210, NOW(), NOW()),
(8, 170, 'BATCH008', 'B3', 17, 270, NOW(), NOW()),
(9, 140, 'BATCH009', 'B4', 14, 240, NOW(), NOW()),
(7, 130, 'BATCH010', 'B5', 13, 230, NOW(), NOW()),
(4, 160, 'BATCH011', 'C1', 16, 260, NOW(), NOW()),
(3, 190, 'BATCH012', 'C2', 19, 290, NOW(), NOW()),
(8, 70, 'BATCH013', 'C3', 7, 170, NOW(), NOW()),
(4, 180, 'BATCH014', 'C4', 18, 280, NOW(), NOW()),
(5, 100, 'BATCH015', 'C5', 10, 200, NOW(), NOW()),
(6, 110, 'BATCH016', 'D1', 11, 210, NOW(), NOW()),
(9, 90, 'BATCH017', 'D2', 9, 190, NOW(), NOW()),
(8, 140, 'BATCH018', 'D3', 14, 240, NOW(), NOW()),
(9, 150, 'BATCH019', 'D4', 15, 250, NOW(), NOW()),
(10, 120, 'BATCH020', 'D5', 12, 220, NOW(), NOW());

INSERT INTO inventory_transactions (inventory_item_id, transaction_type, quantity, transaction_date, expiration_date, notes, supplier_id, employee_id, created_at, updated_at)
VALUES
(1, 'RECEIVED', 50, NOW() - INTERVAL '30 days', NOW() + INTERVAL '180 days', 'Received from Supplier A', 1001, 2001, NOW(), NOW()),
(2, 'SOLD', 30, NOW() - INTERVAL '20 days', NOW() + INTERVAL '170 days', 'Sold to Customer B', NULL, 2002, NOW(), NOW()),
(3, 'EXPIRED', 10, NOW() - INTERVAL '40 days', NOW() - INTERVAL '10 days', 'Expired batch', NULL, 2003, NOW(), NOW()),
(4, 'DAMAGED', 5, NOW() - INTERVAL '15 days', NOW() + INTERVAL '150 days', 'Damaged during handling', NULL, 2004, NOW(), NOW()),
(3, 'RECEIVED', 80, NOW() - INTERVAL '25 days', NOW() + INTERVAL '160 days', 'Received from Supplier C', 1002, 2005, NOW(), NOW()),
(4, 'SOLD', 40, NOW() - INTERVAL '10 days', NOW() + INTERVAL '140 days', 'Sold to Customer D', NULL, 2006, NOW(), NOW()),
(5, 'ADJUSTED', 20, NOW() - INTERVAL '5 days', NOW() + INTERVAL '130 days', 'Inventory adjustment', NULL, 2007, NOW(), NOW()),
(9, 'RECEIVED', 60, NOW() - INTERVAL '35 days', NOW() + INTERVAL '180 days', 'Received from Supplier E', 1003, 2008, NOW(), NOW()),
(6, 'SOLD', 25, NOW() - INTERVAL '12 days', NOW() + INTERVAL '170 days', 'Sold to Customer F', NULL, 2009, NOW(), NOW()),
(1, 'EXPIRED', 15, NOW() - INTERVAL '45 days', NOW() - INTERVAL '15 days', 'Expired batch', NULL, 2010, NOW(), NOW()),
(1, 'DAMAGED', 7, NOW() - INTERVAL '18 days', NOW() + INTERVAL '160 days', 'Damaged during handling', NULL, 2011, NOW(), NOW()),
(2, 'RECEIVED', 100, NOW() - INTERVAL '20 days', NOW() + INTERVAL '180 days', 'Received from Supplier F', 1004, 2012, NOW(), NOW()),
(3, 'SOLD', 55, NOW() - INTERVAL '8 days', NOW() + INTERVAL '170 days', 'Sold to Customer G', NULL, 2013, NOW(), NOW()),
(4, 'ADJUSTED', 30, NOW() - INTERVAL '4 days', NOW() + INTERVAL '130 days', 'Inventory adjustment', NULL, 2014, NOW(), NOW()),
(5, 'RECEIVED', 90, NOW() - INTERVAL '28 days', NOW() + INTERVAL '160 days', 'Received from Supplier G', 1005, 2015, NOW(), NOW()),
(6, 'SOLD', 35, NOW() - INTERVAL '16 days', NOW() + INTERVAL '150 days', 'Sold to Customer H', NULL, 2016, NOW(), NOW()),
(7, 'EXPIRED', 20, NOW() - INTERVAL '50 days', NOW() - INTERVAL '20 days', 'Expired batch', NULL, 2017, NOW(), NOW()),
(8, 'DAMAGED', 10, NOW() - INTERVAL '22 days', NOW() + INTERVAL '140 days', 'Damaged during handling', NULL, 2018, NOW(), NOW()),
(9, 'RECEIVED', 70, NOW() - INTERVAL '32 days', NOW() + INTERVAL '180 days', 'Received from Supplier H', 1006, 2019, NOW(), NOW()),
(10, 'SOLD', 45, NOW() - INTERVAL '14 days', NOW() + INTERVAL '170 days', 'Sold to Customer I', NULL, 2020, NOW(), NOW());
