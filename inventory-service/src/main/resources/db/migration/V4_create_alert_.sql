CREATE TABLE IF NOT EXISTS public.inventory_alerts (
  id varchar(36) PRIMARY KEY,
  inventory_id varchar(36) NOT NULL,
  alert_type varchar(50) NOT NULL,
  severity varchar(50) NOT NULL,
  message text NOT NULL,
  status varchar(50) NOT NULL,
  triggered_at timestamp without time zone NOT NULL,
  resolved_at timestamp without time zone,
  resolved_by varchar(36),
  resolution_notes text,
  created_at timestamp without time zone NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_inventory_alerts_inventory_id ON public.inventory_alerts(inventory_id);
CREATE INDEX IF NOT EXISTS idx_inventory_alerts_alert_type ON public.inventory_alerts(alert_type);
CREATE INDEX IF NOT EXISTS idx_inventory_alerts_status ON public.inventory_alerts(status);
CREATE INDEX IF NOT EXISTS idx_inventory_alerts_triggered_at ON public.inventory_alerts(triggered_at);

CREATE INDEX IF NOT EXISTS idx_inventory_alerts_inventory_status_triggered ON public.inventory_alerts(inventory_id, status, triggered_at);
