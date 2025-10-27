package microservice.inventory_service.internal.inventory.core.inventory.application.cqrs.command;

import microservice.inventory_service.internal.inventory.core.alert.domain.entity.valueobject.AlertId;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.UserId;

public record ResolveInventoryAlertCommand(AlertId alertId, UserId resolvedBy, String resolutionNotes) {
}
