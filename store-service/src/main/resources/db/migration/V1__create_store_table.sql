CREATE TABLE IF NOT EXISTS stores (
    id UUID PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    address VARCHAR(500) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    zip_code VARCHAR(20),
    phone VARCHAR(20),
    email VARCHAR(100),
    status VARCHAR(50) NOT NULL,
    opening_time TIMESTAMP,
    closing_time TIMESTAMP,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_stores_code ON stores(code);
CREATE INDEX idx_stores_city ON stores(city);
CREATE INDEX idx_stores_state ON stores(state);
CREATE INDEX idx_stores_status ON stores(status);