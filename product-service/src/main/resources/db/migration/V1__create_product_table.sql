-- Create products table
CREATE TABLE products (
    id VARCHAR(36) PRIMARY KEY,
    code VARCHAR(50),
    name VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    active_ingredient VARCHAR(150),
    manufacturer VARCHAR(100),
    category VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    barcode VARCHAR(20),
    expiration_date TIMESTAMP,
    manufacture_date TIMESTAMP,
    requires_prescription BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(50) NOT NULL,
    dosage VARCHAR(100),
    administration VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT chk_category CHECK (category IN (
        'ANALGESICS',
        'ANTIBIOTICS',
        'VITAMINS',
        'DERMATOLOGY',
        'CARDIOLOGY',
        'RESPIRATORY',
        'DIGESTIVE',
        'PEDIATRIC',
        'GENERIC',
        'COSMETICS',
        'PERSONAL_CARE',
        'MEDICAL_DEVICES',
        'NOT_CATEGORIZED'
    )),
    CONSTRAINT chk_status CHECK (status IN (
        'ACTIVE',
        'INACTIVE',
        'DISCONTINUED',
        'OUT_OF_STOCK',
        'PENDING_APPROVAL',
        'UKNOWN'
    )),
    CONSTRAINT chk_price_positive CHECK (price >= 0)
);

-- Create product_contraindications table for contraindications collection
CREATE TABLE product_contraindications (
    product_id VARCHAR(36) NOT NULL,
    contraindication VARCHAR(500) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_product_code ON products(code);
CREATE INDEX idx_product_name ON products(name);
CREATE INDEX idx_product_category ON products(category);
CREATE INDEX idx_product_status ON products(status);
CREATE INDEX idx_product_manufacturer ON products(manufacturer);
CREATE INDEX idx_product_barcode ON products(barcode);
CREATE INDEX idx_product_category_status ON products(category, status);
CREATE INDEX idx_product_requires_prescription ON products(requires_prescription);
CREATE INDEX idx_product_created_at ON products(created_at);
CREATE INDEX idx_product_deleted_at ON products(deleted_at);

-- Index for contraindications lookup
CREATE INDEX idx_contraindication_product ON product_contraindications(product_id);
