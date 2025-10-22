package microservice.inventory.application.command;

import microservice.inventory.domain.entity.valueobject.id.AlertId;
import microservice.inventory.domain.entity.valueobject.id.UserId;

public record ResolveInventoryAlertCommand(AlertId alertId, UserId resolvedBy, String resolutionNotes) {
}
