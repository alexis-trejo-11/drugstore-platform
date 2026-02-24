-- V1__create_address_table.sql
-- Description: Create addresses table for address microservice
-- Author: E-commerce Team
-- Version: 1.0

-- Create enum type for user types (optional - PostgreSQL specific)
-- If you prefer not to use enums, you can use VARCHAR with CHECK constraint
-- DO $$ BEGIN
--     CREATE TYPE user_type AS ENUM ('CUSTOMER', 'EMPLOYEE');
-- EXCEPTION
--     WHEN duplicate_object THEN null;
-- END $$;

-- Create addresses table
CREATE TABLE IF NOT EXISTS addresses (
    -- Primary key using UUID
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- User information
    user_id VARCHAR(50) NOT NULL,
    user_type VARCHAR(20) NOT NULL CHECK (user_type IN ('CUSTOMER', 'EMPLOYEE')),
    
    -- Address fields
    street VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    country VARCHAR(2) NOT NULL CHECK (country ~ '^[A-Z]{2}$'), -- ISO 3166-1 alpha-2
    postal_code VARCHAR(20) NOT NULL,
    additional_details VARCHAR(200),
    
    -- Flags
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT addresses_street_not_empty CHECK (LENGTH(TRIM(street)) > 0),
    CONSTRAINT addresses_city_not_empty CHECK (LENGTH(TRIM(city)) > 0),
    CONSTRAINT addresses_state_not_empty CHECK (LENGTH(TRIM(state)) > 0),
    CONSTRAINT addresses_postal_code_not_empty CHECK (LENGTH(TRIM(postal_code)) > 0)
);

-- Create indexes for performance
CREATE INDEX idx_addresses_user_id ON addresses(user_id) WHERE active = true;
CREATE INDEX idx_addresses_user_default ON addresses(user_id, is_default) WHERE active = true AND is_default = true;
CREATE INDEX idx_addresses_country ON addresses(country) WHERE active = true;
CREATE INDEX idx_addresses_created_at ON addresses(created_at DESC);

-- Create composite index for common queries
CREATE INDEX idx_addresses_user_created ON addresses(user_id, created_at DESC) WHERE active = true;

-- Add comment to table
COMMENT ON TABLE addresses IS 'Stores user addresses for the e-commerce platform';
COMMENT ON COLUMN addresses.id IS 'Unique identifier for the address';
COMMENT ON COLUMN addresses.user_id IS 'Reference to the user who owns this address';
COMMENT ON COLUMN addresses.user_type IS 'Type of user (CUSTOMER or EMPLOYEE) - determines address limits';
COMMENT ON COLUMN addresses.street IS 'Street address including number';
COMMENT ON COLUMN addresses.city IS 'City name';
COMMENT ON COLUMN addresses.state IS 'State or province';
COMMENT ON COLUMN addresses.country IS 'ISO 3166-1 alpha-2 country code';
COMMENT ON COLUMN addresses.postal_code IS 'Postal or ZIP code';
COMMENT ON COLUMN addresses.additional_details IS 'Additional address information (apartment, suite, etc.)';
COMMENT ON COLUMN addresses.is_default IS 'Indicates if this is the default address for the user';
COMMENT ON COLUMN addresses.active IS 'Soft delete flag - false means address is deleted';
COMMENT ON COLUMN addresses.created_at IS 'Timestamp when the address was created';
COMMENT ON COLUMN addresses.updated_at IS 'Timestamp when the address was last updated';

-- Create function to automatically update updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger to automatically update updated_at on row update
CREATE TRIGGER update_addresses_updated_at
    BEFORE UPDATE ON addresses
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE OR REPLACE VIEW active_addresses AS
SELECT * FROM addresses WHERE active = true;

