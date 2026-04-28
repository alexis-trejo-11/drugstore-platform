-- Create users table with CHECK constraints for enum-like values
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    hashed_password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    two_factor_id VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_role CHECK (role IN ('CUSTOMER', 'EMPLOYEE', 'ADMIN')),
    CONSTRAINT check_status CHECK (status IN ('PENDING', 'ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED'))
);

-- Create indexes for better query performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone_number ON users(phone_number);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_users_role ON users(role);

