package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.command;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.entity.valueobject.AlertId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.UserId;

public record ResolveInventoryAlertCommand(AlertId alertId, UserId resolvedBy, String resolutionNotes) {
}
