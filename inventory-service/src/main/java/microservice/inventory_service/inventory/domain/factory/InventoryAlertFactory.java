package microservice.inventory_service.inventory.domain.factory;

import microservice.inventory_service.inventory.domain.entity.InventoryAlert;
import microservice.inventory_service.inventory.domain.entity.enums.AlertSeverity;
import microservice.inventory_service.inventory.domain.entity.enums.AlertStatus;
import microservice.inventory_service.inventory.domain.entity.enums.AlertType;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.AlertId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;

import java.time.LocalDateTime;

public class InventoryAlertFactory {

    public static InventoryAlert create(InventoryId inventoryId, AlertType alertType,
                                        AlertSeverity severity, String message) {
        return InventoryAlert.reconstructor()
                .id(AlertId.generate())
                .inventoryId(inventoryId)
                .alertType(alertType)
                .severity(severity)
                .message(message)
                .status(AlertStatus.ACTIVE)
                .triggeredAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .reconstruct();
    }
}
