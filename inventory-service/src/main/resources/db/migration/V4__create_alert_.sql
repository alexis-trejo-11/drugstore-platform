CREATE TABLE IF NOT EXISTS public.inventory_alerts (
  id varchar(36) PRIMARY KEY,
  inventory_id varchar(36) NOT NULL,
  alert_type varchar(50) NOT NULL,
  severity varchar(50) NOT NULL,
  message text NOT NULL,
  status varchar(50) NOT NULL,
  triggered_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  resolved_at TIMESTAMP WITHOUT TIME ZONE,
  resolved_by varchar(36),
  resolution_notes text,
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  deleted_at TIMESTAMP WITHOUT TIME ZONE,
  version integer NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_inventory_alerts_inventory_id ON public.inventory_alerts(inventory_id);
CREATE INDEX IF NOT EXISTS idx_inventory_alerts_alert_type ON public.inventory_alerts(alert_type);
CREATE INDEX IF NOT EXISTS idx_inventory_alerts_status ON public.inventory_alerts(status);
CREATE INDEX IF NOT EXISTS idx_inventory_alerts_triggered_at ON public.inventory_alerts(triggered_at);

CREATE INDEX IF NOT EXISTS idx_inventory_alerts_inventory_status_triggerED ON public.inventory_alerts(inventory_id, status, triggered_at);
