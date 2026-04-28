-- Create addresses table and link to orders table
CREATE TABLE addresses (
    id VARCHAR(36) PRIMARY KEY,
    country VARCHAR(100) NOT NULL,
    city VARCHAR(100),
    state VARCHAR(100) NOT NULL,
    neighborhood VARCHAR(100),
    zip_code VARCHAR(20) NOT NULL,
    street VARCHAR(255) NOT NULL,
    building_type VARCHAR(100),
    inner_number VARCHAR(50),
    outer_number VARCHAR(50),
    additional_info VARCHAR(500),
    user_id VARCHAR(36) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Foreign key
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    -- Constraints
    CONSTRAINT check_address_country CHECK (country IN ('USA', 'CANADA', 'MEXICO')),
    CONSTRAINT check_building_type CHECK (building_type IN ('APARTMENT', 'HOUSE', 'OFFICE', 'CONDOMINIUM', 'WAREHOUSE', 'COMMERCIAL', 'OTHER'))
);

-- Indexes for new table and foreign key
CREATE INDEX idx_addresses_id ON addresses(id);
CREATE INDEX idx_addresses_city ON addresses(city);
CREATE INDEX idx_addresses_state ON addresses(state);
CREATE INDEX idx_addresses_user_id ON addresses(user_id);
-- CREATE UNIQUE INDEX uq_addresses_user_default ON addresses(user_id) WHERE is_default = TRUE;
