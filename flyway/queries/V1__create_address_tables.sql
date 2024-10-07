CREATE TABLE clients_addresses (
    id SERIAL PRIMARY KEY,
    street VARCHAR(255) NOT NULL,
    house_number INT NOT NULL,
    neighborhood VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    country VARCHAR(255)  NOT NULL,
    description TEXT,
    zip_code INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    inner_number VARCHAR(50),
    address_type VARCHAR(50) CHECK (address_type IN ('HOUSE', 'DEPARTMENT')),
    client_id INT NOT NULL
);


INSERT INTO clients_addresses (street, house_number, neighborhood, state, country, description, zip_code, created_at, updated_at, inner_number, address_type, client_id)
VALUES
('Maple Street', 123, 'Downtown', 'California', 'USA', 'Near the city park', 90210, NOW(), NOW(), 'A', 'HOUSE', 1),
('Oak Avenue', 456, 'Westside', 'California', 'USA', 'Quiet neighborhood', 90211, NOW(), NOW(), 'B', 'DEPARTMENT', 1),

('Pine Street', 789, 'Uptown', 'Texas', 'USA', 'Close to shopping center', 75001, NOW(), NOW(), 'C', 'HOUSE', 2),
('Birch Road', 101, 'Midtown', 'Texas', 'USA', 'Near the university', 75002, NOW(), NOW(), 'D', 'DEPARTMENT', 2),

('Elm Drive', 202, 'Eastside', 'Florida', 'USA', 'Beach view', 33101, NOW(), NOW(), 'E', 'HOUSE', 3),
('Cedar Street', 303, 'Southside', 'Florida', 'USA', 'Next to the marina', 33102, NOW(), NOW(), 'F', 'DEPARTMENT', 3),

('Maple Street', 404, 'Central Park', 'New York', 'USA', 'Walking distance to the park', 10001, NOW(), NOW(), 'G', 'HOUSE', 4),
('Cherry Avenue', 505, 'Times Square', 'New York', 'USA', 'In the city center', 10002, NOW(), NOW(), 'H', 'DEPARTMENT', 4),

('Palm Street', 606, 'Sunset Boulevard', 'California', 'USA', 'Great mountain views', 90212, NOW(), NOW(), 'I', 'HOUSE', 5),
('Redwood Lane', 707, 'Hollywood', 'California', 'USA', 'Near the studios', 90213, NOW(), NOW(), 'J', 'DEPARTMENT', 5);

