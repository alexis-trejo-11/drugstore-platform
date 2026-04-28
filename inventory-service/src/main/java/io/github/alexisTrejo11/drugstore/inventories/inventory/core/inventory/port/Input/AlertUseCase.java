package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.port.Input;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.entity.InventoryAlert;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.entity.valueobject.AlertId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.command.ResolveInventoryAlertCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.query.GetInventoryAlertsQuery;

import java.util.List;

public interface AlertUseCase {
    List<InventoryAlert> getActiveAlerts(GetInventoryAlertsQuery query);
    void acknowledgeAlert(AlertId alertId);
    void resolveAlert(ResolveInventoryAlertCommand command);
    void dismissAlert(AlertId alertId);
}