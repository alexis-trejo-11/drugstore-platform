CREATE TABLE stores (
    id VARCHAR(36) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    country VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    neighborhood VARCHAR(100),
    street VARCHAR(200) NOT NULL,
    number VARCHAR(20) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    status VARCHAR(50) NOT NULL,
    schedule_config JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_store_code ON stores(code);
CREATE INDEX idx_store_status ON stores(status);
CREATE INDEX idx_store_location ON stores(latitude, longitude);
CREATE INDEX idx_store_city_status ON stores(city, status);
CREATE INDEX idx_store_schedule ON stores USING gin(schedule_config);