-- Create sequences for auto-increment if needed (optional)
CREATE SEQUENCE IF NOT EXISTS batch_number_seq START 1000 INCREMENT 1;

-- Create inventories table
CREATE TABLE IF NOT EXISTS inventories (
    id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    total_quantity INTEGER NOT NULL DEFAULT 0,
    available_quantity INTEGER NOT NULL DEFAULT 0,
    reserved_quantity INTEGER NOT NULL DEFAULT 0,
    reorder_level INTEGER NOT NULL DEFAULT 0,
    reorder_quantity INTEGER NOT NULL DEFAULT 0,
    maximum_stock_level INTEGER,
    warehouse_location VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    last_restocked_date TIMESTAMP,
    last_count_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_inventories PRIMARY KEY (id),
    CONSTRAINT chk_inventories_positive_quantities CHECK (
        total_quantity >= 0 AND
        available_quantity >= 0 AND
        reserved_quantity >= 0
    ),
    CONSTRAINT chk_inventories_quantity_consistency CHECK (
        total_quantity = available_quantity + reserved_quantity
    ),
    CONSTRAINT chk_inventories_reorder_positive CHECK (
        reorder_level >= 0 AND reorder_quantity > 0
    ),
    CONSTRAINT chk_inventories_status_values CHECK (
        status IN ('ACTIVE','INACTIVE','LOW_STOCK','OUT_OF_STOCK','DISCONTINUED','SUSPENDED')
    )
);

-- Create inventory_batches table
CREATE TABLE IF NOT EXISTS inventory_batches (
    id VARCHAR(36) NOT NULL,
    inventory_id VARCHAR(36) NOT NULL,
    batch_number VARCHAR(100) NOT NULL,
    lot_number VARCHAR(100),
    quantity INTEGER NOT NULL DEFAULT 0,
    available_quantity INTEGER NOT NULL DEFAULT 0,
    cost_per_unit DECIMAL(10,2),
    manufacturing_date TIMESTAMP,
    expiration_date TIMESTAMP NOT NULL,
    supplier_id VARCHAR(36),
    supplier_name VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    storage_conditions TEXT,
    received_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_inventory_batches PRIMARY KEY (id),
    CONSTRAINT chk_batches_positive_quantities CHECK (
        quantity >= 0 AND available_quantity >= 0
    ),
    CONSTRAINT chk_batches_quantity_consistency CHECK (
        available_quantity <= quantity
    ),
    CONSTRAINT chk_batches_dates CHECK (
        manufacturing_date IS NULL OR expiration_date > manufacturing_date
    ),
    CONSTRAINT chk_batches_status_values CHECK (
        status IN ('ACTIVE','EXPIRED','RECALLED','DAMAGED','DEPLETED')
    )
);

-- Create inventory_movements table
CREATE TABLE IF NOT EXISTS inventory_movements (
    id VARCHAR(36) NOT NULL,
    inventory_id VARCHAR(36) NOT NULL,
    batch_id VARCHAR(36),
    movement_type VARCHAR(20) NOT NULL,
    quantity INTEGER NOT NULL,
    previous_quantity INTEGER,
    new_quantity INTEGER,
    reason VARCHAR(500),
    reference_id VARCHAR(36) NOT NULL,
    reference_type VARCHAR(100),
    performed_by VARCHAR(36) NOT NULL,
    notes TEXT,
    movement_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_inventory_movements PRIMARY KEY (id),
    CONSTRAINT chk_movements_positive_quantity CHECK (quantity > 0),
    CONSTRAINT chk_movements_quantity_consistency CHECK (
        (previous_quantity IS NOT NULL AND new_quantity IS NOT NULL) OR
        (previous_quantity IS NULL AND new_quantity IS NULL)
    ),
    CONSTRAINT chk_movements_type_values CHECK (
        movement_type IN ('RECEIPT','SALE','RETURN','ADJUSTMENT','TRANSFER','DAMAGE','EXPIRATION','RESERVATION','RELEASE')
    )
);

-- Create indexes for better performance
-- Inventories table indexes
CREATE UNIQUE INDEX IF NOT EXISTS idx_inventories_product_id ON inventories(product_id);
CREATE INDEX IF NOT EXISTS idx_inventories_status ON inventories(status);
CREATE INDEX IF NOT EXISTS idx_inventories_warehouse_location ON inventories(warehouse_location);
CREATE INDEX IF NOT EXISTS idx_inventories_created_at ON inventories(created_at);
-- Partial index for active reorder level
CREATE INDEX IF NOT EXISTS idx_inventories_reorder_level ON inventories(reorder_level) WHERE status = 'ACTIVE';

-- Inventory batches table indexes
CREATE UNIQUE INDEX IF NOT EXISTS idx_batches_batch_number ON inventory_batches(batch_number);
CREATE INDEX IF NOT EXISTS idx_batches_inventory_id ON inventory_batches(inventory_id);
CREATE INDEX IF NOT EXISTS idx_batches_status ON inventory_batches(status);
CREATE INDEX IF NOT EXISTS idx_batches_expiration_date ON inventory_batches(expiration_date);
CREATE INDEX IF NOT EXISTS idx_batches_supplier_id ON inventory_batches(supplier_id);
CREATE INDEX IF NOT EXISTS idx_batches_created_at ON inventory_batches(created_at);
CREATE INDEX IF NOT EXISTS idx_batches_lot_number ON inventory_batches(lot_number);

-- Inventory movements table indexes
CREATE INDEX IF NOT EXISTS idx_movements_inventory_id ON inventory_movements(inventory_id);
CREATE INDEX IF NOT EXISTS idx_movements_batch_id ON inventory_movements(batch_id);
CREATE INDEX IF NOT EXISTS idx_movements_movement_type ON inventory_movements(movement_type);
CREATE INDEX IF NOT EXISTS idx_movements_reference_id ON inventory_movements(reference_id);
CREATE INDEX IF NOT EXISTS idx_movements_performed_by ON inventory_movements(performed_by);
CREATE INDEX IF NOT EXISTS idx_movements_movement_date ON inventory_movements(movement_date);
CREATE INDEX IF NOT EXISTS idx_movements_created_at ON inventory_movements(created_at);
CREATE INDEX IF NOT EXISTS idx_movements_reference_type ON inventory_movements(reference_type);

-- Create foreign key constraints
ALTER TABLE IF EXISTS inventory_batches
ADD CONSTRAINT IF NOT EXISTS fk_batches_inventory_id
FOREIGN KEY (inventory_id) REFERENCES inventories(id)
ON DELETE CASCADE;

ALTER TABLE IF EXISTS inventory_movements
ADD CONSTRAINT IF NOT EXISTS fk_movements_inventory_id
FOREIGN KEY (inventory_id) REFERENCES inventories(id)
ON DELETE CASCADE;

ALTER TABLE IF EXISTS inventory_movements
ADD CONSTRAINT IF NOT EXISTS fk_movements_batch_id
FOREIGN KEY (batch_id) REFERENCES inventory_batches(id)
ON DELETE SET NULL;

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers for updated_at (drop existing first to avoid errors on re-run)
DROP TRIGGER IF EXISTS update_inventories_updated_at ON inventories;
CREATE TRIGGER update_inventories_updated_at
    BEFORE UPDATE ON inventories
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_inventory_batches_updated_at ON inventory_batches;
CREATE TRIGGER update_inventory_batches_updated_at
    BEFORE UPDATE ON inventory_batches
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Create function to validate inventory quantities
CREATE OR REPLACE FUNCTION validate_inventory_quantities()
RETURNS TRIGGER AS $$
BEGIN
    -- Ensure available_quantity doesn't exceed total_quantity
    IF NEW.available_quantity > NEW.total_quantity THEN
        RAISE EXCEPTION 'Available quantity cannot exceed total quantity';
    END IF;

    -- Ensure reserved_quantity doesn't exceed total_quantity
    IF NEW.reserved_quantity > NEW.total_quantity THEN
        RAISE EXCEPTION 'Reserved quantity cannot exceed total quantity';
    END IF;

    -- Ensure sum of available and reserved equals total
    IF NEW.available_quantity + NEW.reserved_quantity != NEW.total_quantity THEN
        RAISE EXCEPTION 'Available quantity + Reserved quantity must equal Total quantity';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for inventory quantity validation
DROP TRIGGER IF EXISTS validate_inventory_quantities_trigger ON inventories;
CREATE TRIGGER validate_inventory_quantities_trigger
    BEFORE INSERT OR UPDATE ON inventories
    FOR EACH ROW
    EXECUTE FUNCTION validate_inventory_quantities();

-- Create function to validate batch quantities
CREATE OR REPLACE FUNCTION validate_batch_quantities()
RETURNS TRIGGER AS $$
BEGIN
    -- Ensure available_quantity doesn't exceed quantity
    IF NEW.available_quantity > NEW.quantity THEN
        RAISE EXCEPTION 'Batch available quantity cannot exceed total quantity';
    END IF;

    -- Ensure batch is not expired if status is ACTIVE
    IF NEW.status = 'ACTIVE' AND NEW.expiration_date <= CURRENT_TIMESTAMP THEN
        RAISE EXCEPTION 'Active batch cannot have expired expiration date';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for batch quantity validation
DROP TRIGGER IF EXISTS validate_batch_quantities_trigger ON inventory_batches;
CREATE TRIGGER validate_batch_quantities_trigger
    BEFORE INSERT OR UPDATE ON inventory_batches
    FOR EACH ROW
    EXECUTE FUNCTION validate_batch_quantities();

-- Create function to update inventory status based on quantities
CREATE OR REPLACE FUNCTION update_inventory_status()
RETURNS TRIGGER AS $$
BEGIN
    -- Update status based on available_quantity and reorder_level
    IF NEW.available_quantity = 0 THEN
        NEW.status = 'OUT_OF_STOCK';
    ELSIF NEW.available_quantity <= NEW.reorder_level THEN
        NEW.status = 'LOW_STOCK';
    ELSE
        NEW.status = 'ACTIVE';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for automatic inventory status updates
DROP TRIGGER IF EXISTS update_inventory_status_trigger ON inventories;
CREATE TRIGGER update_inventory_status_trigger
    BEFORE INSERT OR UPDATE OF available_quantity, reorder_level ON inventories
    FOR EACH ROW
    EXECUTE FUNCTION update_inventory_status();

-- Create function to update batch status based on expiration
CREATE OR REPLACE FUNCTION update_batch_status()
RETURNS TRIGGER AS $$
BEGIN
    -- Update batch status if expired
    IF NEW.expiration_date <= CURRENT_TIMESTAMP AND NEW.status = 'ACTIVE' THEN
        NEW.status = 'EXPIRED';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for automatic batch status updates
DROP TRIGGER IF EXISTS update_batch_status_trigger ON inventory_batches;
CREATE TRIGGER update_batch_status_trigger
    BEFORE INSERT OR UPDATE OF expiration_date ON inventory_batches
    FOR EACH ROW
    EXECUTE FUNCTION update_batch_status();

-- Create comments for documentation
COMMENT ON TABLE inventories IS 'Stores inventory information for products';
COMMENT ON TABLE inventory_batches IS 'Stores batch/lot information for inventory items';
COMMENT ON TABLE inventory_movements IS 'Audit trail for all inventory movements and transactions';