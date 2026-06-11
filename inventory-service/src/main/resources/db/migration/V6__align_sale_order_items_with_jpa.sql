-- sale_order_items (V3): used column `quantity`; JPA expects ordered_quantity, received_quantity,
-- and BaseEntity audit columns (created_at, updated_at, deleted_at, version).

ALTER TABLE sale_order_items ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now();
ALTER TABLE sale_order_items ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now();
ALTER TABLE sale_order_items ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE sale_order_items ADD COLUMN IF NOT EXISTS version INTEGER NOT NULL DEFAULT 1;

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'public' AND table_name = 'sale_order_items' AND column_name = 'quantity'
  ) THEN
    ALTER TABLE sale_order_items RENAME COLUMN quantity TO ordered_quantity;
  END IF;
END$$;

ALTER TABLE sale_order_items ADD COLUMN IF NOT EXISTS received_quantity INTEGER NOT NULL DEFAULT 0;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'pk_sale_order_items') THEN
    ALTER TABLE sale_order_items ADD CONSTRAINT pk_sale_order_items PRIMARY KEY (id);
  END IF;
END$$;
