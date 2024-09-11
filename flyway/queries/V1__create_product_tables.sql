-- Enums
CREATE TYPE route_of_administration AS ENUM (
    'ORAL', 'SPREAD', 'TOPICAL', 'TRANSDERMAL', 'INTRAMUSCULAR',
    'INTRAVENOUS', 'SUBCUTANEOUS', 'RECTAL', 'VAGINAL', 'OPHTHALMIC',
    'OTIC', 'NASAL', 'INHALATION', 'BUCCAL', 'SUBLINGUAL'
);

CREATE TYPE product_presentation AS ENUM (
    'BOX', 'BOTTLE', 'TUBE', 'BLISTER', 'SACHET', 'VIAL', 'JAR',
    'AMPOULE', 'SPRAY', 'DROPPER', 'CARTON', 'BAG', 'POUCH',
    'CANISTER', 'SYRINGE', 'PATCH', 'INHALER', 'DOSE_PACK', 'KIT', 'STICK'
);

CREATE TYPE product_type AS ENUM (
    'CREAM', 'PILL', 'TABLET', 'CAPSULE', 'SYRUP', 'OINTMENT', 'GEL',
    'DROPS', 'INJECTION', 'INHALER', 'PATCH', 'SUPPOSITORY', 'LOZENGE',
    'POWDER', 'LIQUID', 'SUSPENSION', 'EMULSION', 'SOLUTION', 'GRANULES',
    'SPRAY', 'FOAM', 'SHAMPOO', 'SOAP', 'TINCTURE', 'PASTE', 'BANDAGE',
    'BALM', 'SUPPLEMENT', 'VITAMIN', 'HERBAL', 'TOPICAL', 'ORAL', 'RECTAL',
    'VAGINAL', 'NASAL', 'AEROSOL', 'CHEWABLE', 'EFFERVESCENT', 'TRANSDERMAL',
    'GELCAP', 'MOUTHWASH', 'GROCERIES', 'DIAPER', 'DISPOSABLE_DIAPER',
    'CONDOM', 'PROPHYLACTIC', 'PREGNANCY_TEST', 'PREGNANCY_TEST_KIT'
);

-- Create Tables
CREATE TABLE main_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    main_category_id BIGINT,
    FOREIGN KEY (main_category_id) REFERENCES main_categories(id) ON DELETE SET NULL
);

CREATE TABLE subcategories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    main_category_id BIGINT,
    category_id BIGINT,
    FOREIGN KEY (main_category_id) REFERENCES main_categories(id) ON DELETE SET NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE TABLE brands (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE suppliers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact_info VARCHAR(255),
    address VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE category_specifications (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT,
    name VARCHAR(255) NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    image VARCHAR(255),
    price DECIMAL(10, 2),
    upc VARCHAR(50),
    content TEXT,
    package_dimension VARCHAR(255),
    route_of_administration VARCHAR(20) CHECK (route_of_administration IN ('ORAL', 'SPREAD', 'TOPICAL', 'TRANSDERMAL', 'INTRAMUSCULAR', 'INTRAVENOUS', 'SUBCUTANEOUS', 'RECTAL', 'VAGINAL', 'OPHTHALMIC', 'OTIC', 'NASAL', 'INHALATION', 'BUCCAL', 'SUBLINGUAL')),
    product_presentation VARCHAR(20) CHECK (product_presentation IN ('BOX', 'BOTTLE', 'TUBE', 'BLISTER', 'SACHET', 'VIAL', 'JAR', 'AMPOULE', 'SPRAY', 'DROPPER', 'CARTON', 'BAG', 'POUCH', 'CANISTER', 'SYRINGE', 'PATCH', 'INHALER', 'DOSE_PACK', 'KIT', 'STICK')),
    product_type VARCHAR(20) CHECK (product_type IN ('CREAM', 'PILL', 'TABLET', 'CAPSULE', 'SYRUP', 'OINTMENT', 'GEL', 'DROPS', 'INJECTION', 'INHALER', 'PATCH', 'SUPPOSITORY', 'LOZENGE', 'POWDER', 'LIQUID', 'SUSPENSION', 'EMULSION', 'SOLUTION', 'GRANULES', 'SPRAY', 'FOAM', 'SHAMPOO', 'SOAP', 'TINCTURE', 'PASTE', 'BANDAGE', 'BALM', 'SUPPLEMENT', 'VITAMIN', 'HERBAL', 'TOPICAL', 'ORAL', 'RECTAL', 'VAGINAL', 'NASAL', 'AEROSOL', 'CHEWABLE', 'EFFERVESCENT', 'TRANSDERMAL', 'GELCAP', 'MOUTHWASH', 'GROCERIES', 'DIAPER', 'DISPOSABLE_DIAPER', 'CONDOM', 'PROPHYLACTIC', 'PREGNANCY_TEST', 'PREGNANCY_TEST_KIT')),
    prescription_required BOOLEAN,
    age_usage VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    main_category_id BIGINT,
    category_id BIGINT,
    subcategory_id BIGINT,
    supplier_id BIGINT,
    brand_id BIGINT,
    FOREIGN KEY (main_category_id) REFERENCES main_categories(id) ON DELETE SET NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (subcategory_id) REFERENCES subcategories(id) ON DELETE SET NULL,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE SET NULL,
    FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE SET NULL
);


CREATE TABLE product_attributes (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT,
    name VARCHAR(255) NOT NULL,
    value VARCHAR(255) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Insert data into main_category
INSERT INTO main_categories (name, created_at, updated_at) VALUES
('Medicines', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Personal Care', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Baby Care', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Vitamins & Supplements', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('First Aid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert data into categories
INSERT INTO categories (name, created_at, updated_at, main_category_id) VALUES
('Pain Relief', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
('Skincare', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2),
('Diapers', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3),
('Cold & Flu', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
('Multivitamins', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4),
('Bandages', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5),
('Oral Care', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2);

-- Insert data into subcategory
INSERT INTO subcategories (name, created_at, updated_at, main_category_id, category_id) VALUES
('Headache', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1),
('Moisturizers', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 2),
('Newborn', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 3),
('Cough Syrup', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 4),
('Adult Multivitamins', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4, 5),
('Adhesive Bandages', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 6),
('Toothpaste', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 7);

-- Insert data into brand
INSERT INTO brands (name, created_at, updated_at) VALUES
('Tylenol', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Neutrogena', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Pampers', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Robitussin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Centrum', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Band-Aid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Colgate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert data into supplier
INSERT INTO suppliers (name, contact_info, address, phone, email, created_at, updated_at) VALUES
('PharmaCorp', 'John Doe', '123 Pharma St, MedCity', '555-1234', 'john@pharmacorp.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('BeautyCo', 'Jane Smith', '456 Beauty Ave, CosmoTown', '555-5678', 'jane@beautyco.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('BabySupplies Inc', 'Bob Johnson', '789 Baby Blvd, Infantville', '555-9012', 'bob@babysupplies.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('VitaminWorld', 'Alice Brown', '101 Nutrient Rd, Healthville', '555-3456', 'alice@vitaminworld.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MedEquip Co', 'Charlie Davis', '202 First Aid Ln, SafetyCity', '555-7890', 'charlie@medequip.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert data into category_specification
INSERT INTO category_specifications (category_id, name) VALUES
(1, 'Active Ingredient'),
(2, 'Skin Type'),
(3, 'Size'),
(4, 'Form'),
(5, 'Age Group'),
(6, 'Material'),
(7, 'Flavor');

-- Insert data into products
INSERT INTO products (name, image, price, upc, content, package_dimension, route_of_administration, product_type, product_presentation, prescription_required, age_usage, created_at, updated_at, main_category_id, category_id, subcategory_id, supplier_id, brand_id) VALUES
('Extra Strength Tylenol', 'tylenol.jpg', 9.99, '300450123456', 'For headache and pain relief', '4x2x2 inches', 'ORAL', 'TABLET', 'BOTTLE', false, 'Adults and children over 12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 1, 1, 1),
('Neutrogena Hydro Boost', 'neutrogena.jpg', 15.99, '300450789012', 'Hydrating facial moisturizer', '2x2x4 inches', 'TOPICAL', 'CREAM', 'JAR', false, 'All ages', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 2, 2, 2, 2),
('Pampers Swaddlers', 'pampers.jpg', 24.99, '300450345678', 'Ultra-absorbent diapers for newborns', '12x8x6 inches', 'TOPICAL', 'DISPOSABLE_DIAPER', 'BAG', false, 'Newborns up to 10 lbs', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 3, 3, 3, 3),
('Robitussin Cough Syrup', 'robitussin.jpg', 8.99, '300450901234', 'Relieves cough and chest congestion', '3x2x6 inches', 'ORAL', 'SYRUP', 'BOTTLE', false, 'Adults and children over 6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 4, 4, 1, 4),
('Centrum Adult Multivitamin', 'centrum.jpg', 12.99, '300450567890', 'Complete multivitamin for adults', '4x2x2 inches', 'ORAL', 'TABLET', 'BOTTLE', false, 'Adults', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4, 5, 5, 4, 5),
('Band-Aid Flexible Fabric Adhesive Bandages', 'bandaid.jpg', 4.99, '300450234567', 'Flexible fabric bandages for minor wounds', '4x3x1 inches', 'TOPICAL', 'BANDAGE', 'BOX', false, 'All ages', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 6, 6, 5, 6),
('Colgate Total Whitening Toothpaste', 'colgate.jpg', 3.99, '300450678901', 'Whitening toothpaste for complete oral care', '2x1x6 inches', 'ORAL', 'PASTE', 'TUBE', false, 'Adults and children over 2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 7, 7, 2, 7),
('Advil Liqui-Gels', 'advil.jpg', 7.99, '300450135790', 'Fast acting pain relief', '3x2x2 inches', 'ORAL', 'GELCAP', 'BOTTLE', false, 'Adults and children over 12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 1, 1, 1),
('Cetaphil Gentle Skin Cleanser', 'cetaphil.jpg', 10.99, '300450246801', 'Gentle, non-irritating facial cleanser', '3x3x8 inches', 'TOPICAL', 'LIQUID', 'BOTTLE', false, 'All ages', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 2, 2, 2, 2),
('Huggies Little Snugglers', 'huggies.jpg', 22.99, '300450357912', 'Gentle diapers for newborns', '12x8x6 inches', 'TOPICAL', 'DISPOSABLE_DIAPER', 'BAG', false, 'Newborns up to 10 lbs', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 3, 3, 3, 3);

-- Insert data into product_attribute
INSERT INTO product_attributes (product_id, name, value) VALUES
(1, 'Strength', '500mg'),
(1, 'Quantity', '100 tablets'),
(2, 'Volume', '50ml'),
(2, 'Key Ingredient', 'Hyaluronic Acid'),
(3, 'Count', '84 diapers'),
(3, 'Absorbency', 'Up to 12 hours'),
(4, 'Volume', '118ml'),
(4, 'Active Ingredient', 'Dextromethorphan'),
(5, 'Count', '130 tablets'),
(5, 'Key Nutrients', 'Vitamins A, C, D, E, B6, B12'),
(6, 'Count', '30 bandages'),
(6, 'Size', 'Assorted'),
(7, 'Volume', '150ml'),
(7, 'Key Feature', 'Whitening'),
(8, 'Strength', '200mg'),
(8, 'Quantity', '80 liquid gels'),
(9, 'Volume', '500ml'),
(9, 'Skin Type', 'All skin types'),
(10, 'Count', '88 diapers'),
(10, 'Feature', 'Wetness indicator');