package microservice.inventory_service.internal.core.inventory.application.cqrs.command;

import microservice.inventory_service.internal.core.alert.domain.entity.valueobject.AlertId;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.UserId;

public record ResolveInventoryAlertCommand(AlertId alertId, UserId resolvedBy, String resolutionNotes) {
}
