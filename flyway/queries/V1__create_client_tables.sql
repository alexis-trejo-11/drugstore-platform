CREATE TABLE clients (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    birth_date DATE,
    phone VARCHAR(20),
    is_active BOOLEAN,
    is_client_premium BOOLEAN,
    loyalty_points INT,
    joined_at TIMESTAMP,
    last_action TIMESTAMP
);

CREATE TABLE addresses (
    id SERIAL PRIMARY KEY,
    street VARCHAR(255),
    house_number INT,
    neighborhood VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    country VARCHAR(255),
    description TEXT,
    zip_code INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    inner_number VARCHAR(50),
    address_type VARCHAR(50) CHECK (address_type IN ('HOUSE', 'DEPARTMENT')),
    client_id INT REFERENCES clients(id) ON DELETE CASCADE
);


INSERT INTO clients (first_name, last_name, birth_date, phone, is_active, is_client_premium, loyalty_points, joined_at, last_action)
VALUES
('Alice', 'Johnson', '1990-03-25', '555-1234', TRUE, TRUE, 250, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Bob', 'Smith', '1982-07-12', '555-5678', TRUE, FALSE, 180, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Carol', 'Williams', '1975-11-30', '555-8765', FALSE, FALSE, 90, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('David', 'Brown', '1988-02-14', '555-4321', TRUE, TRUE, 300, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Eva', 'Miller', '1995-05-20', '555-6789', TRUE, TRUE, 400, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Frank', 'Davis', '1979-08-08', '555-9876', FALSE, FALSE, 50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Grace', 'Garcia', '1983-09-17', '555-1357', TRUE, TRUE, 220, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Henry', 'Martinez', '1992-10-04', '555-2468', TRUE, FALSE, 150, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Ivy', 'Rodriguez', '1986-12-22', '555-3698', TRUE, TRUE, 320, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Jack', 'Wilson', '1998-06-15', '555-6543', TRUE, TRUE, 280, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO addresses (street, house_number, neighborhood, city, state, country, description, zip_code, created_at, updated_at, inner_number, address_type, client_id)
VALUES
('123 Oak St', 12, 'Greenwood', 'Springfield', 'IL', 'USA', 'Near the park', 62701, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Apt 2A', 'DEPARTMENT', 1),
('456 Pine St', 34, 'Maplewood', 'Springfield', 'IL', 'USA', 'Close to downtown', 62702, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Suite 101', 'HOUSE', 2),
('789 Birch St', 56, 'Riverwood', 'Springfield', 'IL', 'USA', 'Next to the river', 62703, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Apt 3C', 'DEPARTMENT', 3),
('321 Cedar St', 78, 'Lakeview', 'Springfield', 'IL', 'USA', 'Near the lake', 62704, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Bungalow 5', 'HOUSE', 4),
('654 Elm St', 90, 'Forest Hill', 'Springfield', 'IL', 'USA', 'Quiet neighborhood', 62705, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Apt 1D', 'DEPARTMENT', 5),
('987 Willow St', 11, 'Sunset Valley', 'Springfield', 'IL', 'USA', 'Great sunset views', 62706, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Cottage 2', 'HOUSE', 6),
('246 Redwood St', 23, 'Mountainview', 'Springfield', 'IL', 'USA', 'Overlooks the mountains', 62707, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Apt 5E', 'DEPARTMENT', 7),
('369 Aspen St', 45, 'Hilltop', 'Springfield', 'IL', 'USA', 'Highest point in town', 62708, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Villa 3', 'HOUSE', 8),
('135 Spruce St', 67, 'Seaside', 'Springfield', 'IL', 'USA', 'By the sea', 62709, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Apt 4F', 'DEPARTMENT', 9),
('246 Cherry St', 89, 'Riverside', 'Springfield', 'IL', 'USA', 'Next to the river', 62710, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'House 1', 'HOUSE', 10);
