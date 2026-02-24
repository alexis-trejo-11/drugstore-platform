-- =====================================================
-- Employee Service - Database Schema
-- =====================================================

-- Create employees table
CREATE TABLE employees (
    id VARCHAR(255) PRIMARY KEY,
    employee_number VARCHAR(20) NOT NULL UNIQUE,

    -- Personal Information
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,

    -- Contact Information (Embeddable)
    email VARCHAR(255),
    phone_number VARCHAR(50),
    emergency_contact VARCHAR(255),
    emergency_phone VARCHAR(50),

    -- Workday Schedule (JSON)
    workday_schedule JSONB,

    -- Employment Details
    role VARCHAR(50) NOT NULL CHECK (role IN (
        'PHARMACIST',
        'PHARMACY_TECHNICIAN',
        'STORE_MANAGER',
        'ASSISTANT_MANAGER',
        'CASHIER',
        'INVENTORY_CLERK',
        'DELIVERY_DRIVER',
        'CUSTOMER_SERVICE_REP',
        'JANITOR'
    )),
    employee_type VARCHAR(20) NOT NULL CHECK (employee_type IN (
        'FULL_TIME',
        'PART_TIME',
        'CONTRACTOR',
        'INTERN',
        'SEASONAL'
    )),
    status VARCHAR(20) NOT NULL CHECK (status IN (
        'ACTIVE',
        'INACTIVE',
        'ON_LEAVE',
        'SUSPENDED',
        'TERMINATED'
    )),
    department VARCHAR(100),
    store_id VARCHAR(50),
    hire_date DATE NOT NULL,
    termination_date DATE,

    -- Compensation
    hourly_rate DECIMAL(10, 2) NOT NULL,
    weekly_hours INTEGER NOT NULL,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),

    -- Soft delete
    deleted_at TIMESTAMP
);

-- Create indexes for employees table
CREATE UNIQUE INDEX idx_employee_number ON employees(employee_number);
CREATE INDEX idx_employee_status ON employees(status);
CREATE INDEX idx_employee_role ON employees(role);
CREATE INDEX idx_employee_store ON employees(store_id);
CREATE INDEX idx_employee_deleted ON employees(deleted_at);
CREATE INDEX idx_employee_hire_date ON employees(hire_date);
CREATE INDEX idx_employee_email ON employees(email);

-- Create employee_certifications table
CREATE TABLE employee_certifications (
    id VARCHAR(255) PRIMARY KEY,
    employee_id VARCHAR(255) NOT NULL,
    license_number VARCHAR(255) NOT NULL,
    issuing_authority VARCHAR(255) NOT NULL,
    issue_date DATE NOT NULL,
    expiration_date DATE NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN (
        'PHARMACIST_LICENSE',
        'PHARMACY_TECHNICIAN_CERTIFICATION',
        'CPR_CERTIFICATION',
        'FIRST_AID_CERTIFICATION',
        'IMMUNIZATION_CERTIFICATION',
        'OTHER'
    )),
    CONSTRAINT fk_certification_employee FOREIGN KEY (employee_id)
        REFERENCES employees(id) ON DELETE CASCADE
);

-- Create indexes for certifications table
CREATE INDEX idx_certification_employee ON employee_certifications(employee_id);
CREATE INDEX idx_certification_expiration ON employee_certifications(expiration_date);
CREATE INDEX idx_certification_type ON employee_certifications(type);

-- Add comments for documentation
COMMENT ON TABLE employees IS 'Stores employee information including personal details, employment information, and compensation';
COMMENT ON TABLE employee_certifications IS 'Stores certifications and licenses for employees';

COMMENT ON COLUMN employees.employee_number IS 'Unique employee identification number';
COMMENT ON COLUMN employees.deleted_at IS 'Soft delete timestamp - if not null, employee is marked as deleted';
COMMENT ON COLUMN employees.hourly_rate IS 'Employee hourly compensation rate';
COMMENT ON COLUMN employees.weekly_hours IS 'Standard weekly working hours';

