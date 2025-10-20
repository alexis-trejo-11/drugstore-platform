package microservice.inventory.domain.port.output;

import microservice.inventory.domain.entity.InventoryAlert;
import microservice.inventory.domain.entity.enums.AlertSeverity;
import microservice.inventory.domain.entity.enums.AlertStatus;
import microservice.inventory.domain.entity.enums.AlertType;
import microservice.inventory.domain.entity.valueobject.id.AlertId;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface InventoryAlertRepository {
    InventoryAlert save(InventoryAlert alert);

    Optional<InventoryAlert> findById(AlertId id);

    Page<InventoryAlert> findByInventoryId(AlertId inventoryId);

    Page<InventoryAlert> findByType(AlertType type);

    Page<InventoryAlert> findByStatus(AlertStatus status);

    Page<InventoryAlert> findBySeverity(AlertSeverity severity);

    Page<InventoryAlert> findActiveAlerts();

    void delete(AlertId id);
}
