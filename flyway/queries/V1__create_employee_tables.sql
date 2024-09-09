-- Create Position Table
CREATE TABLE positions (
    id SERIAL PRIMARY KEY,
    position_name VARCHAR(255),
    salary DECIMAL(15, 2),
    classification_workday VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Employee Table
CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    genre VARCHAR(50),
    birth_date DATE,
    company_email VARCHAR(255),
    company_phone VARCHAR(50),
    hired_at TIMESTAMP,
    fired_at TIMESTAMP,
    is_employee_active BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    position_id INT,
    FOREIGN KEY (position_id) REFERENCES positions(id) ON DELETE CASCADE
);


-- Create PhoneNumber Table
CREATE TABLE phone_numbers (
    id SERIAL PRIMARY KEY,
    number VARCHAR(50),
    phone_company VARCHAR(255),
    created_at TIMESTAMP,
    assigned_at TIMESTAMP,
    employee_id INT UNIQUE,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);


-- Insert into Position Table
INSERT INTO positions (position_name, salary, classification_workday, created_at, updated_at)
VALUES ('Manager', 75000.00, 'FULL_TIME', NOW(), NOW());

INSERT INTO positions (position_name, salary, classification_workday, created_at, updated_at)
VALUES ('Pharmacist', 65000.00, 'FULL_TIME', NOW(), NOW());

INSERT INTO positions (position_name, salary, classification_workday, created_at, updated_at)
VALUES ('Technician', 40000.00, 'PART_TIME', NOW(), NOW());

INSERT INTO positions (position_name, salary, classification_workday, created_at, updated_at)
VALUES ('Cashier', 30000.00, 'PART_TIME', NOW(), NOW());

INSERT INTO positions (position_name, salary, classification_workday, created_at, updated_at)
VALUES ('Inventory Specialist', 45000.00, 'FULL_TIME', NOW(), NOW());

-- Insert into Employee Table
INSERT INTO employees (first_name, last_name, genre, birth_date, company_email, company_phone, hired_at, fired_at, is_employee_active, created_at, updated_at, position_id)
VALUES
    ('John', 'Doe', 'MALE', '1985-05-15', 'john.doe@company.com', '555-1234', NOW(), NULL, TRUE, NOW(), NOW(), 1),
    ('Jane', 'Smith', 'FEMALE', '1990-07-20', 'jane.smith@company.com', '555-5678', NOW(), NULL, TRUE, NOW(), NOW(), 2),
    ('Alice', 'Johnson', 'FEMALE', '1988-02-10', 'alice.johnson@company.com', '555-2345', NOW(), NULL, TRUE, NOW(), NOW(), 3),
    ('Bob', 'Brown', 'MALE', '1975-11-22', 'bob.brown@company.com', '555-3456', NOW(), NULL, TRUE, NOW(), NOW(), 4),
    ('Eve', 'Davis', 'FEMALE', '1992-08-30', 'eve.davis@company.com', '555-4567', NOW(), NULL, TRUE, NOW(), NOW(), 5),
    ('Charlie', 'Miller', 'MALE', '1982-03-05', 'charlie.miller@company.com', '555-6789', NOW(), NULL, TRUE, NOW(), NOW(), 1),
    ('Diana', 'Wilson', 'FEMALE', '1987-06-14', 'diana.wilson@company.com', '555-7890', NOW(), NULL, TRUE, NOW(), NOW(), 2),
    ('Frank', 'Taylor', 'MALE', '1995-12-25', 'frank.taylor@company.com', '555-8901', NOW(), NULL, TRUE, NOW(), NOW(), 3),
    ('Grace', 'Anderson', 'FEMALE', '1989-09-18', 'grace.anderson@company.com', '555-9012', NOW(), NULL, TRUE, NOW(), NOW(), 4),
    ('Henry', 'Thomas', 'MALE', '1979-01-11', 'henry.thomas@company.com', '555-0123', NOW(), NULL, TRUE, NOW(), NOW(), 5),
    ('Ivy', 'Jackson', 'FEMALE', '1993-04-07', 'ivy.jackson@company.com', '555-2346', NOW(), NULL, TRUE, NOW(), NOW(), 1),
    ('Jack', 'White', 'MALE', '1984-12-31', 'jack.white@company.com', '555-3457', NOW(), NULL, TRUE, NOW(), NOW(), 2),
    ('Kelly', 'Harris', 'FEMALE', '1997-05-22', 'kelly.harris@company.com', '555-4568', NOW(), NULL, TRUE, NOW(), NOW(), 3),
    ('Liam', 'Martin', 'MALE', '1981-10-19', 'liam.martin@company.com', '555-5679', NOW(), NULL, TRUE, NOW(), NOW(), 4),
    ('Mia', 'Thompson', 'FEMALE', '1990-11-29', 'mia.thompson@company.com', '555-6780', NOW(), NULL, TRUE, NOW(), NOW(), 5),
    ('Nathan', 'Garcia', 'MALE', '1977-02-16', 'nathan.garcia@company.com', '555-7891', NOW(), NULL, TRUE, NOW(), NOW(), 1),
    ('Olivia', 'Martinez', 'FEMALE', '1983-07-03', 'olivia.martinez@company.com', '555-8902', NOW(), NULL, TRUE, NOW(), NOW(), 2),
    ('Peter', 'Robinson', 'MALE', '1996-01-27', 'peter.robinson@company.com', '555-9013', NOW(), NULL, TRUE, NOW(), NOW(), 3);

-- Insert into PhoneNumber Table
INSERT INTO phone_numbers (number, phone_company, created_at, assigned_at, employee_id)
VALUES
    ('555-0001', 'Verizon', NOW(), NOW(), 1),
    ('555-0002', 'AT&T', NOW(), NOW(), 2),
    ('555-0003', 'T-Mobile', NOW(), NOW(), 3),
    ('555-0004', 'Sprint', NOW(), NOW(), 4),
    ('555-0005', 'Verizon', NOW(), NOW(), 5),
    ('555-0006', 'AT&T', NOW(), NOW(), 6),
    ('555-0007', 'T-Mobile', NOW(), NOW(), 7),
    ('555-0008', 'Sprint', NOW(), NOW(), 8),
    ('555-0009', 'Verizon', NOW(), NOW(), 9),
    ('555-0010', 'AT&T', NOW(), NOW(), 10);
