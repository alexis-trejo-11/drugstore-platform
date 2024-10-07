CREATE TABLE clients (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    birth_date DATE NOT NULL,
    phone_number VARCHAR(20),
    email VARCHAR(100) NOT NULL,
    is_active BOOLEAN  NOT NULL,
    is_client_premium BOOLEAN  NOT NULL,
    loyalty_points INT NOT NULL,
    last_action TIMESTAMP NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

INSERT INTO clients (first_name, last_name, birth_date, phone_number, email, is_active, is_client_premium, loyalty_points, last_action, created_at, updated_at)
VALUES
    ('John', 'Doe', '1985-04-12', '555-1234', 'john.doe@example.com', TRUE, FALSE, 150, '2024-10-07 10:15:00', '2024-01-10 09:00:00', '2024-10-07 10:15:00'),
    ('Jane', 'Smith', '1990-08-23', '555-5678', 'jane.smith@example.com', TRUE, TRUE, 320, '2024-10-06 15:30:00', '2023-12-15 11:25:00', '2024-10-06 15:30:00'),
    ('Alice', 'Johnson', '1975-02-18', '555-9876', 'alice.johnson@example.com', FALSE, FALSE, 0, '2024-09-30 08:45:00', '2024-01-01 08:00:00', '2024-09-30 08:45:00'),
    ('Bob', 'Williams', '1982-11-05', '555-4321', 'bob.williams@example.com', TRUE, FALSE, 75, '2024-10-07 12:00:00', '2024-03-22 14:10:00', '2024-10-07 12:00:00'),
    ('Emily', 'Davis', '1995-07-30', '555-6789', 'emily.davis@example.com', TRUE, TRUE, 500, '2024-10-01 17:00:00', '2024-04-05 09:45:00', '2024-10-01 17:00:00'),
    ('Michael', 'Brown', '1988-06-14', '555-2468', 'michael.brown@example.com', FALSE, FALSE, 0, '2024-09-15 10:20:00', '2024-02-12 12:30:00', '2024-09-15 10:20:00'),
    ('Jessica', 'Miller', '1979-03-09', '555-1357', 'jessica.miller@example.com', TRUE, TRUE, 215, '2024-10-05 14:50:00', '2024-01-20 13:15:00', '2024-10-05 14:50:00'),
    ('Chris', 'Garcia', '1993-12-02', '555-8642', 'chris.garcia@example.com', TRUE, FALSE, 90, '2024-09-25 16:40:00', '2024-05-10 08:05:00', '2024-09-25 16:40:00'),
    ('Laura', 'Martinez', '2000-05-20', '555-9753', 'laura.martinez@example.com', TRUE, TRUE, 280, '2024-10-04 09:30:00', '2024-06-01 11:50:00', '2024-10-04 09:30:00'),
    ('David', 'Taylor', '1992-01-15', '555-3698', 'david.taylor@example.com', TRUE, FALSE, 60, '2024-10-02 11:15:00', '2024-03-08 07:25:00', '2024-10-02 11:15:00');
