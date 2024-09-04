-- Create Position Table
CREATE TABLE position (
    id SERIAL PRIMARY KEY,
    position_name VARCHAR(255),
    salary DECIMAL(15, 2),
    classification_workday VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create Employee Table
CREATE TABLE employee (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    genre VARCHAR(50),
    birth_date DATE,
    company_email VARCHAR(255),
    company_phone VARCHAR(50),
    hired_at TIMESTAMP,
    fired_at TIMESTAMP,
    address TEXT,
    is_employee_active BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    position_id INT,
    FOREIGN KEY (position_id) REFERENCES position(id) ON DELETE CASCADE
);

-- Create PhoneNumber Table
CREATE TABLE phone_numbers (
    id SERIAL PRIMARY KEY,
    number VARCHAR(50),
    phone_company VARCHAR(255),
    created_at TIMESTAMP,
    assigned_at TIMESTAMP,
    employee_id INT UNIQUE,
    FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE
);


-- Insert into Position Table
INSERT INTO position (position_name, salary, classification_workday, created_at, updated_at)
VALUES ('Manager', 75000.00, 'FULL_TIME', NOW(), NOW());

INSERT INTO position (position_name, salary, classification_workday, created_at, updated_at)
VALUES ('Pharmacist', 65000.00, 'FULL_TIME', NOW(), NOW());

INSERT INTO position (position_name, salary, classification_workday, created_at, updated_at)
VALUES ('Technician', 40000.00, 'PART_TIME', NOW(), NOW());

INSERT INTO position (position_name, salary, classification_workday, created_at, updated_at)
VALUES ('Cashier', 30000.00, 'PART_TIME', NOW(), NOW());

INSERT INTO position (position_name, salary, classification_workday, created_at, updated_at)
VALUES ('Inventory Specialist', 45000.00, 'FULL_TIME', NOW(), NOW());

-- Insert into Employee Table
INSERT INTO employee (first_name, last_name, genre, birth_date, company_email, company_phone, hired_at, fired_at, address, is_employee_active, created_at, updated_at, position_id)
VALUES ('John', 'Doe', 'MALE', '1985-05-15', 'john.doe@company.com', '555-1234', NOW(), NULL, '123 Main St, City', TRUE, NOW(), NOW(), 1);

INSERT INTO employee (first_name, last_name, genre, birth_date, company_email, company_phone, hired_at, fired_at, address, is_employee_active, created_at, updated_at, position_id)
VALUES ('Jane', 'Smith', 'FEMALE', '1990-07-20', 'jane.smith@company.com', '555-5678', NOW(), NULL, '456 Oak Ave, Town', TRUE, NOW(), NOW(), 2);

INSERT INTO employee (first_name, last_name, genre, birth_date, company_email, company_phone, hired_at, fired_at, address, is_employee_active, created_at, updated_at, position_id)
VALUES ('Alice', 'Johnson', 'FEMALE', '1988-02-10', 'alice.johnson@company.com', '555-2345', NOW(), NULL, '789 Pine Rd, Village', TRUE, NOW(), NOW(), 3);

INSERT INTO employee (first_name, last_name, genre, birth_date, company_email, company_phone, hired_at, fired_at, address, is_employee_active, created_at, updated_at, position_id)
VALUES ('Bob', 'Brown', 'MALE', '1975-11-22', 'bob.brown@company.com', '555-3456', NOW(), NULL, '101 Maple Dr, City', TRUE, NOW(), NOW(), 4);

INSERT INTO employee (first_name, last_name, genre, birth_date, company_email, company_phone, hired_at, fired_at, address, is_employee_active, created_at, updated_at, position_id)
VALUES ('Eve', 'Davis', 'FEMALE', '1992-08-30', 'eve.davis@company.com', '555-4567', NOW(), NULL, '202 Birch Ln, Town', TRUE, NOW(), NOW(), 5);

INSERT INTO employee (first_name, last_name, genre, birth_date, company_email, company_phone, hired_at, fired_at, address, is_employee_active, created_at, updated_at, position_id)
VALUES ('Charlie', 'Miller', 'MALE', '1982-03-05', 'charlie.miller@company.com', '555-6789', NOW(), NULL, '303 Cedar St, Village', TRUE, NOW(), NOW(), 1);

INSERT INTO employee (first_name, last_name, genre, birth_date, company_email, company_phone, hired_at, fired_at, address, is_employee_active, created_at, updated_at, position_id)
VALUES ('Diana', 'Wilson', 'FEMALE', '1987-06-14', 'diana.wilson@company.com', '555-7890', NOW(), NULL, '404 Elm Ave, City', TRUE, NOW(), NOW(), 2);

INSERT INTO employee (first_name, last_name, genre, birth_date, company_email, company_phone, hired_at, fired_at, address, is_employee_active, created_at, updated_at, position_id)
VALUES ('Frank', 'Taylor', 'MALE', '1995-12-25', 'frank.taylor@company.com', '555-8901', NOW(), NULL, '505 Willow Rd, Town', TRUE, NOW(), NOW(), 3);

INSERT INTO employee (first_name, last_name, genre, birth_date, company_email, company_phone, hired_at, fired_at, address, is_employee_active, created_at, updated_at, position_id)
VALUES ('Grace', 'Anderson', 'FEMALE', '1989-09-18', 'grace.anderson@company.com', '555-9012', NOW(), NULL, '606 Fir Dr, Village', TRUE, NOW(), NOW(), 4);

INSERT INTO employee (first_name, last_name, genre, birth_date, company_email, company_phone, hired_at, fired_at, address, is_employee_active, created_at, updated_at, position_id)
VALUES ('Henry', 'Thomas', 'MALE', '1979-01-11', 'henry.thomas@company.com', '555-0123', NOW(), NULL, '707 Spruce Ln, City', TRUE, NOW(), NOW(), 5);

INSERT INTO employee (first_name, last_name, genre, birth_date, company_email, company_phone, hired_at, fired_at, address, is_employee_active, created_at, updated_at, position_id)
VALUES ('Ivy', 'Jackson', 'FEMALE', '1993-04-07', 'ivy.jackson@company.com', '555-2346', NOW(), NULL, '808 Ash Ave, Town', TRUE, NOW(), NOW(), 1);

INSERT INTO employee (first_name, last_name, genre, birth_date, company_email, company_phone, hired_at, fired_at, address, is_employee_active, created_at, updated_at, position_id)
VALUES ('Jack', 'White', 'MALE', '1984-12-31', 'jack.white@company.com', '555-3457', NOW(), NULL, '909 Maple St, Village', TRUE, NOW(), NOW(), 2);

INSERT INTO employee (first_name, last_name, genre, birth_date, company_email, company_phone, hired_at, fired_at, address, is_employee_active, created_at, updated_at, position_id)
VALUES ('Kelly', 'Harris', 'FEMALE', '1997-05-22', 'kelly.harris@company.com', '555-4568', NOW(), NULL, '1010 Birch Rd, City', TRUE, NOW(), NOW(), 3);

INSERT INTO employee (first_name, last_name, genre, birth_date, company_email, company_phone, hired_at, fired_at, address, is_employee_active, created_at, updated_at, position_id)
VALUES ('Liam', 'Martin', 'MALE', '1981-10-19', 'liam.martin@company.com', '555-5679', NOW(), NULL, '1111 Oak Ave, Town', TRUE, NOW(), NOW(), 4);

INSERT INTO employee (first_name, last_name, genre, birth_date, company_email, company_phone, hired_at, fired_at, address, is_employee_active, created_at, updated_at, position_id)
VALUES ('Mia', 'Thompson', 'FEMALE', '1990-11-29', 'mia.thompson@company.com', '555-6780', NOW(), NULL, '1212 Pine Dr, Village', TRUE, NOW(), NOW(), 5);

INSERT INTO employee (first_name, last_name, genre, birth_date, company_email, company_phone, hired_at, fired_at, address, is_employee_active, created_at, updated_at, position_id)
VALUES ('Nathan', 'Garcia', 'MALE', '1977-02-16', 'nathan.garcia@company.com', '555-7891', NOW(), NULL, '1313 Maple St, City', TRUE, NOW(), NOW(), 1);

INSERT INTO employee (first_name, last_name, genre, birth_date, company_email, company_phone, hired_at, fired_at, address, is_employee_active, created_at, updated_at, position_id)
VALUES ('Olivia', 'Martinez', 'FEMALE', '1983-07-03', 'olivia.martinez@company.com', '555-8902', NOW(), NULL, '1414 Fir Rd, Town', TRUE, NOW(), NOW(), 2);

INSERT INTO employee (first_name, last_name, genre, birth_date, company_email, company_phone, hired_at, fired_at, address, is_employee_active, created_at, updated_at, position_id)
VALUES ('Peter', 'Robinson', 'MALE', '1996-01-27', 'peter.robinson@company.com', '555-9013', NOW(), NULL, '1515 Cedar Ln, Village', TRUE, NOW(), NOW(), 3);

-- Insert into PhoneNumber Table
INSERT INTO phone_numbers (number, phone_company, created_at, assigned_at, employee_id)
VALUES ('555-0001', 'Verizon', NOW(), NOW(), 1);

INSERT INTO phone_numbers (number, phone_company, created_at, assigned_at, employee_id)
VALUES ('555-0002', 'AT&T', NOW(), NOW(), 2);

INSERT INTO phone_numbers (number, phone_company, created_at, assigned_at, employee_id)
VALUES ('555-0003', 'T-Mobile', NOW(), NOW(), 3);

INSERT INTO phone_numbers (number, phone_company, created_at, assigned_at, employee_id)
VALUES ('555-0004', 'Sprint', NOW(), NOW(), 4);

INSERT INTO phone_numbers (number, phone_company, created_at, assigned_at, employee_id)
VALUES ('555-0005', 'Verizon', NOW(), NOW(), 5);

INSERT INTO phone_numbers (number, phone_company, created_at, assigned_at, employee_id)
VALUES ('555-0006', 'AT&T', NOW(), NOW(), 6);

INSERT INTO phone_numbers (number, phone_company, created_at, assigned_at, employee_id)
VALUES ('555-0007', 'T-Mobile', NOW(), NOW(), 7);

INSERT INTO phone_numbers (number, phone_company, created_at, assigned_at, employee_id)
VALUES ('555-0008', 'Sprint', NOW(), NOW(), 8);

INSERT INTO phone_numbers (number, phone_company, created_at, assigned_at, employee_id)
VALUES ('555-0009', 'Verizon', NOW(), NOW(), 9);

INSERT INTO phone_numbers (number, phone_company, created_at, assigned_at, employee_id)
VALUES ('555-0010', 'AT&T', NOW(), NOW(), 10);
