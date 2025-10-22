package microservice.inventory_service.inventory.application.command;

import microservice.inventory_service.inventory.domain.entity.valueobject.id.AlertId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.UserId;

public record ResolveInventoryAlertCommand(AlertId alertId, UserId resolvedBy, String resolutionNotes) {
}
