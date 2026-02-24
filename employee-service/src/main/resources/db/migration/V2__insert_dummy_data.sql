-- =====================================================
-- Employee Service - Dummy Data
-- =====================================================

-- Insert 15 employees with diverse roles and information
INSERT INTO employees (
    id, employee_number, first_name, last_name, date_of_birth,
    email, phone_number, emergency_contact, emergency_phone,
    workday_schedule,
    role, employee_type, status, department, store_id,
    hire_date, termination_date, hourly_rate, weekly_hours,
    created_at, updated_at, created_by, last_modified_by, deleted_at
) VALUES
-- 1. Senior Pharmacist
(
    'emp-001', 'EMP001', 'Maria', 'Garcia', '1985-03-15',
    'maria.garcia@drugstore.com', '+1-555-0101', 'Carlos Garcia', '+1-555-0102',
    '{"monday": "09:00-17:00", "tuesday": "09:00-17:00", "wednesday": "09:00-17:00", "thursday": "09:00-17:00", "friday": "09:00-17:00"}'::jsonb,
    'PHARMACIST', 'FULL_TIME', 'ACTIVE', 'Pharmacy', 'store-001',
    '2018-01-15', NULL, 45.50, 40,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', NULL
),
-- 2. Pharmacy Technician
(
    'emp-002', 'EMP002', 'John', 'Smith', '1992-07-22',
    'john.smith@drugstore.com', '+1-555-0201', 'Jane Smith', '+1-555-0202',
    '{"monday": "10:00-18:00", "tuesday": "10:00-18:00", "wednesday": "10:00-18:00", "thursday": "10:00-18:00", "friday": "10:00-18:00"}'::jsonb,
    'PHARMACY_TECHNICIAN', 'FULL_TIME', 'ACTIVE', 'Pharmacy', 'store-001',
    '2020-03-10', NULL, 22.75, 40,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', NULL
),
-- 3. Store Manager
(
    'emp-003', 'EMP003', 'Sarah', 'Johnson', '1988-11-05',
    'sarah.johnson@drugstore.com', '+1-555-0301', 'Michael Johnson', '+1-555-0302',
    '{"monday": "08:00-17:00", "tuesday": "08:00-17:00", "wednesday": "08:00-17:00", "thursday": "08:00-17:00", "friday": "08:00-17:00", "saturday": "09:00-13:00"}'::jsonb,
    'STORE_MANAGER', 'FULL_TIME', 'ACTIVE', 'Management', 'store-002',
    '2017-06-01', NULL, 38.00, 45,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', NULL
),
-- 4. Assistant Manager
(
    'emp-004', 'EMP004', 'David', 'Martinez', '1990-04-18',
    'david.martinez@drugstore.com', '+1-555-0401', 'Lisa Martinez', '+1-555-0402',
    '{"monday": "12:00-21:00", "tuesday": "12:00-21:00", "wednesday": "12:00-21:00", "thursday": "12:00-21:00", "friday": "12:00-21:00"}'::jsonb,
    'ASSISTANT_MANAGER', 'FULL_TIME', 'ACTIVE', 'Management', 'store-002',
    '2019-08-15', NULL, 28.50, 40,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', NULL
),
-- 5. Senior Cashier
(
    'emp-005', 'EMP005', 'Emily', 'Brown', '1995-09-30',
    'emily.brown@drugstore.com', '+1-555-0501', 'Robert Brown', '+1-555-0502',
    '{"monday": "09:00-17:00", "tuesday": "09:00-17:00", "wednesday": "09:00-17:00", "thursday": "09:00-17:00", "friday": "09:00-17:00"}'::jsonb,
    'CASHIER', 'FULL_TIME', 'ACTIVE', 'Customer Service', 'store-003',
    '2021-02-01', NULL, 18.50, 40,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', NULL
),
-- 6. Part-time Cashier
(
    'emp-006', 'EMP006', 'Michael', 'Wilson', '1998-12-12',
    'michael.wilson@drugstore.com', '+1-555-0601', 'Susan Wilson', '+1-555-0602',
    '{"monday": "14:00-19:00", "wednesday": "14:00-19:00", "friday": "14:00-19:00", "saturday": "10:00-15:00"}'::jsonb,
    'CASHIER', 'PART_TIME', 'ACTIVE', 'Customer Service', 'store-003',
    '2022-05-20', NULL, 16.00, 25,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', NULL
),
-- 7. Inventory Clerk
(
    'emp-007', 'EMP007', 'Jennifer', 'Davis', '1993-06-25',
    'jennifer.davis@drugstore.com', '+1-555-0701', 'Thomas Davis', '+1-555-0702',
    '{"monday": "06:00-14:00", "tuesday": "06:00-14:00", "wednesday": "06:00-14:00", "thursday": "06:00-14:00", "friday": "06:00-14:00"}'::jsonb,
    'INVENTORY_CLERK', 'FULL_TIME', 'ACTIVE', 'Inventory', 'store-004',
    '2020-09-10', NULL, 20.00, 40,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', NULL
),
-- 8. Delivery Driver
(
    'emp-008', 'EMP008', 'Robert', 'Anderson', '1987-02-14',
    'robert.anderson@drugstore.com', '+1-555-0801', 'Patricia Anderson', '+1-555-0802',
    '{"monday": "08:00-16:00", "tuesday": "08:00-16:00", "wednesday": "08:00-16:00", "thursday": "08:00-16:00", "friday": "08:00-16:00"}'::jsonb,
    'DELIVERY_DRIVER', 'FULL_TIME', 'ACTIVE', 'Delivery', 'store-004',
    '2019-11-05', NULL, 19.50, 40,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', NULL
),
-- 9. Customer Service Representative
(
    'emp-009', 'EMP009', 'Jessica', 'Taylor', '1996-08-20',
    'jessica.taylor@drugstore.com', '+1-555-0901', 'Daniel Taylor', '+1-555-0902',
    '{"monday": "09:00-17:00", "tuesday": "09:00-17:00", "wednesday": "09:00-17:00", "thursday": "09:00-17:00", "friday": "09:00-17:00"}'::jsonb,
    'CUSTOMER_SERVICE_REP', 'FULL_TIME', 'ACTIVE', 'Customer Service', 'store-005',
    '2021-07-12', NULL, 17.75, 40,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', NULL
),
-- 10. Pharmacist on Leave
(
    'emp-010', 'EMP010', 'Christopher', 'Thomas', '1983-01-08',
    'christopher.thomas@drugstore.com', '+1-555-1001', 'Amanda Thomas', '+1-555-1002',
    NULL,
    'PHARMACIST', 'FULL_TIME', 'ON_LEAVE', 'Pharmacy', 'store-005',
    '2016-04-20', NULL, 47.00, 40,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', NULL
),
-- 11. Intern Pharmacy Technician
(
    'emp-011', 'EMP011', 'Ashley', 'Moore', '2001-10-03',
    'ashley.moore@drugstore.com', '+1-555-1101', 'Brian Moore', '+1-555-1102',
    '{"tuesday": "13:00-17:00", "thursday": "13:00-17:00", "saturday": "10:00-14:00"}'::jsonb,
    'PHARMACY_TECHNICIAN', 'INTERN', 'ACTIVE', 'Pharmacy', 'store-001',
    '2023-06-01', NULL, 15.00, 20,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', NULL
),
-- 12. Seasonal Cashier
(
    'emp-012', 'EMP012', 'Matthew', 'Jackson', '1999-05-17',
    'matthew.jackson@drugstore.com', '+1-555-1201', 'Nancy Jackson', '+1-555-1202',
    '{"wednesday": "12:00-18:00", "friday": "12:00-18:00", "saturday": "10:00-16:00", "sunday": "10:00-16:00"}'::jsonb,
    'CASHIER', 'SEASONAL', 'ACTIVE', 'Customer Service', 'store-001',
    '2023-11-15', NULL, 16.50, 30,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', NULL
),
-- 13. Janitor
(
    'emp-013', 'EMP013', 'Linda', 'White', '1980-03-28',
    'linda.white@drugstore.com', '+1-555-1301', 'Richard White', '+1-555-1302',
    '{"monday": "18:00-22:00", "tuesday": "18:00-22:00", "wednesday": "18:00-22:00", "thursday": "18:00-22:00", "friday": "18:00-22:00", "saturday": "16:00-20:00"}'::jsonb,
    'JANITOR', 'FULL_TIME', 'ACTIVE', 'Facilities', 'store-002',
    '2015-09-01', NULL, 18.00, 40,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', NULL
),
-- 14. Contractor IT Support
(
    'emp-014', 'EMP014', 'James', 'Harris', '1991-07-11',
    'james.harris@drugstore.com', '+1-555-1401', 'Karen Harris', '+1-555-1402',
    '{"monday": "09:00-17:00", "tuesday": "09:00-17:00", "wednesday": "09:00-17:00", "thursday": "09:00-17:00", "friday": "09:00-17:00"}'::jsonb,
    'CUSTOMER_SERVICE_REP', 'CONTRACTOR', 'ACTIVE', 'IT Support', 'store-003',
    '2022-01-10', NULL, 35.00, 40,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', NULL
),
-- 15. Terminated Employee (Soft Deleted)
(
    'emp-015', 'EMP015', 'Patricia', 'Martin', '1989-11-22',
    'patricia.martin@drugstore.com', '+1-555-1501', 'Steven Martin', '+1-555-1502',
    NULL,
    'CASHIER', 'FULL_TIME', 'TERMINATED', 'Customer Service', 'store-003',
    '2020-02-15', '2023-12-31', 17.00, 40,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', '2023-12-31 17:30:00'
);

-- Insert certifications for relevant employees
INSERT INTO employee_certifications (
    id, employee_id, license_number, issuing_authority,
    issue_date, expiration_date, type
) VALUES
-- Certifications for Maria Garcia (Pharmacist)
(
    'cert-001', 'emp-001', 'RPH-CA-123456', 'California Board of Pharmacy',
    '2018-01-01', '2027-01-01', 'PHARMACIST_LICENSE'
),
(
    'cert-002', 'emp-001', 'IMM-CA-789012', 'California Immunization Coalition',
    '2020-06-15', '2026-06-15', 'IMMUNIZATION_CERTIFICATION'
),
(
    'cert-003', 'emp-001', 'CPR-AHA-345678', 'American Heart Association',
    '2024-03-20', '2026-03-20', 'CPR_CERTIFICATION'
),
-- Certifications for John Smith (Pharmacy Technician)
(
    'cert-004', 'emp-002', 'PTCB-901234', 'Pharmacy Technician Certification Board',
    '2020-02-15', '2026-02-15', 'PHARMACY_TECHNICIAN_CERTIFICATION'
),
(
    'cert-005', 'emp-002', 'CPR-AHA-567890', 'American Heart Association',
    '2023-08-10', '2025-08-10', 'CPR_CERTIFICATION'
),
-- Certifications for Sarah Johnson (Store Manager)
(
    'cert-006', 'emp-003', 'CPR-AHA-234567', 'American Heart Association',
    '2024-01-15', '2026-01-15', 'CPR_CERTIFICATION'
),
(
    'cert-007', 'emp-003', 'FA-RC-890123', 'Red Cross',
    '2024-01-15', '2026-01-15', 'FIRST_AID_CERTIFICATION'
),
-- Certifications for Christopher Thomas (Pharmacist on Leave)
(
    'cert-008', 'emp-010', 'RPH-CA-654321', 'California Board of Pharmacy',
    '2016-04-01', '2027-04-01', 'PHARMACIST_LICENSE'
),
(
    'cert-009', 'emp-010', 'IMM-CA-456789', 'California Immunization Coalition',
    '2019-09-20', '2025-09-20', 'IMMUNIZATION_CERTIFICATION'
),
(
    'cert-010', 'emp-010', 'CPR-AHA-012345', 'American Heart Association',
    '2023-11-05', '2025-11-05', 'CPR_CERTIFICATION'
),
-- Certification for Ashley Moore (Intern)
(
    'cert-011', 'emp-011', 'PTCB-INTERN-111111', 'Pharmacy Technician Certification Board',
    '2023-06-01', '2025-06-01', 'PHARMACY_TECHNICIAN_CERTIFICATION'
),
-- Certifications for Emily Brown (Cashier)
(
    'cert-012', 'emp-005', 'CPR-AHA-678901', 'American Heart Association',
    '2023-05-10', '2025-05-10', 'CPR_CERTIFICATION'
),
-- Certifications for David Martinez (Assistant Manager)
(
    'cert-013', 'emp-004', 'CPR-AHA-789012', 'American Heart Association',
    '2024-02-20', '2026-02-20', 'CPR_CERTIFICATION'
),
(
    'cert-014', 'emp-004', 'FA-RC-123456', 'Red Cross',
    '2024-02-20', '2026-02-20', 'FIRST_AID_CERTIFICATION'
),
-- Certification for Jennifer Davis (Inventory Clerk)
(
    'cert-015', 'emp-007', 'OTHER-INV-555555', 'National Inventory Association',
    '2021-03-15', '2026-03-15', 'OTHER'
),
-- Certifications for Robert Anderson (Delivery Driver)
(
    'cert-016', 'emp-008', 'CPR-AHA-456123', 'American Heart Association',
    '2023-09-10', '2025-09-10', 'CPR_CERTIFICATION'
),
(
    'cert-017', 'emp-008', 'FA-RC-789456', 'Red Cross',
    '2023-09-10', '2025-09-10', 'FIRST_AID_CERTIFICATION'
),
-- Certification for Jessica Taylor (Customer Service Rep)
(
    'cert-018', 'emp-009', 'CPR-AHA-321654', 'American Heart Association',
    '2024-06-15', '2026-06-15', 'CPR_CERTIFICATION'
),
-- Certifications for Matthew Jackson (Seasonal Cashier)
(
    'cert-019', 'emp-012', 'CPR-AHA-987321', 'American Heart Association',
    '2023-12-01', '2025-12-01', 'CPR_CERTIFICATION'
),
-- Certification for Linda White (Janitor)
(
    'cert-020', 'emp-013', 'OTHER-CLEAN-222222', 'Professional Cleaning Association',
    '2016-01-10', '2027-01-10', 'OTHER'
),
-- Certifications for James Harris (Contractor IT Support)
(
    'cert-021', 'emp-014', 'CPR-AHA-654987', 'American Heart Association',
    '2022-08-20', '2024-08-20', 'CPR_CERTIFICATION'
),
-- Additional Certifications for Michael Wilson (Part-time Cashier)
(
    'cert-022', 'emp-006', 'FA-RC-135792', 'Red Cross',
    '2023-01-15', '2025-01-15', 'FIRST_AID_CERTIFICATION'
),
-- Expired Certification for Patricia Martin (Terminated Employee)
(
    'cert-023', 'emp-015', 'CPR-AHA-999888', 'American Heart Association',
    '2020-03-01', '2022-03-01', 'CPR_CERTIFICATION'
),
-- Additional immunization certification for Maria Garcia
(
    'cert-024', 'emp-001', 'FA-RC-111222', 'Red Cross',
    '2024-03-20', '2026-03-20', 'FIRST_AID_CERTIFICATION'
),
-- Additional certification for Christopher Thomas (on leave)
(
    'cert-025', 'emp-010', 'FA-RC-333444', 'Red Cross',
    '2023-11-05', '2025-11-05', 'FIRST_AID_CERTIFICATION'
);

-- Add statistics and useful comments
COMMENT ON TABLE employee_certifications IS 'Total certifications inserted: 25 for 11 out of 15 employees. Includes active, expiring, and expired certifications for realistic scenarios.';

