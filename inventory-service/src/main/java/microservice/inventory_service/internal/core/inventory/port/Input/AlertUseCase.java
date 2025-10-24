package microservice.inventory_service.internal.core.inventory.port.Input;

import microservice.inventory_service.internal.core.alert.domain.entity.InventoryAlert;
import microservice.inventory_service.internal.core.alert.domain.entity.valueobject.AlertId;
import microservice.inventory_service.internal.core.inventory.application.cqrs.command.ResolveInventoryAlertCommand;
import microservice.inventory_service.internal.core.inventory.application.cqrs.query.GetInventoryAlertsQuery;

import java.util.List;

public interface AlertUseCase {
    List<InventoryAlert> getActiveAlerts(GetInventoryAlertsQuery query);
    void acknowledgeAlert(AlertId alertId);
    void resolveAlert(ResolveInventoryAlertCommand command);
    void dismissAlert(AlertId alertId);
}