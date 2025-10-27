-- Purschase Orders
CREATE TABLE IF NOT EXISTS public.purchase_order (
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
  purchase_order_id varchar(36) NOT NULL,
  product_id varchar(36) NOT NULL,
  product_name varchar(255) NOT NULL,
  ordered_quantity integer NOT NULL,
  received_quantity integer NOT NULL,
  unit_cost numeric(10,2) NOT NULL,
  total_cost numeric(12,2) NOT NULL,
  batch_number varchar(100)
);

CREATE TABLE IF NOT EXISTS sale_orders (
  id varchar(36) PRIMARY KEY,
  delivery_method varchar(30) NOT NULL,
  status varchar(30) NOT NULL,
  notes varchar(500),
  user_id varchar(36),
  payment_id varchar(36),
  delivery_info_id varchar(36),
  pickup_info_id varchar(36),
  created_at timestamp WITHOUT time zone NOT NULL DEFAULT now(),
  updated_at timestamp WITHOUT time zone
);

ALTER TABLE sale_orders
  ADD CONSTRAINT chk_delivery_method
    CHECK (delivery_method IN (
      'STORE_PICKUP',
      'EXPRESS_DELIVERY',
      'STANDARD_DELIVERY'
    ));

ALTER TABLE sale_orders
  ADD CONSTRAINT chk_order_status
    CHECK (status IN (
      'PENDING_APPROVAL',
      'APPROVED',
      'CANCELLED',
      'PARTIALLY_FULFILLED',
      'FULFILLED',
      'READY_FOR_LEAVE',
      'DELIVERED'
    ));

-- sale_order_items
CREATE TABLE IF NOT EXISTS sale_order_items (
  id varchar(36) NOT NULL,
  order_id varchar(36) NOT NULL,
  product_id varchar(36) NOT NULL,
  product_name varchar(255) NOT NULL,
  quantity integer NOT NULL CHECK (quantity > 0),
  CONSTRAINT fk_sale_order
    FOREIGN KEY (order_id) REFERENCES sale_orders(id) ON DELETE CASCADE
);

-- Índices recomendados
CREATE INDEX IF NOT EXISTS idx_sale_orders_user_id ON sale_orders(user_id);
CREATE INDEX IF NOT EXISTS idx_sale_orders_payment_id ON sale_orders(payment_id);
CREATE INDEX IF NOT EXISTS idx_sale_orders_created_at ON sale_orders(created_at);
CREATE INDEX IF NOT EXISTS idx_sale_orders_status ON sale_orders(status);

CREATE INDEX IF NOT EXISTS idx_sale_order_items_order_id ON sale_order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_sale_order_items_product_id ON sale_order_items(product_id);


-- Add foreign key constraint safely (only if not exists)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_purchase_order_items_order'
    ) THEN
        ALTER TABLE public.purchase_order_items
        ADD CONSTRAINT fk_purchase_order_items_order
        FOREIGN KEY (purchase_order_id) REFERENCES public.purchase_order(id) ON DELETE CASCADE;
    END IF;
END$$;

CREATE INDEX IF NOT EXISTS idx_purchase_order_items_order_id ON public.purchase_order_items(purchase_order_id);
