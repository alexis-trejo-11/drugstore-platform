-- Orders
CREATE TABLE IF NOT EXISTS public.orders (
  id varchar(36) PRIMARY KEY,
  order_number varchar(100) NOT NULL UNIQUE,
  supplier_id varchar(36) NOT NULL,
  supplier_name varchar(255) NOT NULL,
  total_amount numeric(12,2) NOT NULL,
  currency char(3) NOT NULL,
  status varchar(50) NOT NULL,
  order_date timestamp without time zone NOT NULL,
  expected_delivery_date timestamp without time zone,
  actual_delivery_date timestamp without time zone,
  delivery_location varchar(255),
  created_by varchar(36) NOT NULL,
  approved_by varchar(36),
  created_at timestamp without time zone NOT NULL DEFAULT now(),
  updated_at timestamp without time zone NOT NULL DEFAULT now()
);

-- Purchase order items
CREATE TABLE IF NOT EXISTS public.purchase_order_items (
  id varchar(36) PRIMARY KEY,
  order_id varchar(36) NOT NULL,
  product_id varchar(36) NOT NULL,
  product_name varchar(255) NOT NULL,
  ordered_quantity integer NOT NULL,
  received_quantity integer NOT NULL,
  unit_cost numeric(10,2) NOT NULL,
  total_cost numeric(12,2) NOT NULL,
  batch_number varchar(100)
);

-- Add foreign key constraint safely (only if not exists)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_purchase_order_items_order'
    ) THEN
        ALTER TABLE public.purchase_order_items
        ADD CONSTRAINT fk_purchase_order_items_order
        FOREIGN KEY (order_id) REFERENCES public.orders(id) ON DELETE CASCADE;
    END IF;
END$$;

CREATE INDEX IF NOT EXISTS idx_purchase_order_items_order_id ON public.purchase_order_items(order_id);
