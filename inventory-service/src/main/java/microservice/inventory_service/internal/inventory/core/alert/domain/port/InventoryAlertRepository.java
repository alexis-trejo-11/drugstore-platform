package microservice.inventory_service.internal.inventory.core.alert.domain.port;

import microservice.inventory_service.internal.inventory.core.alert.domain.entity.InventoryAlert;
import microservice.inventory_service.internal.inventory.core.alert.domain.entity.valueobject.AlertSeverity;
import microservice.inventory_service.internal.inventory.core.alert.domain.entity.valueobject.AlertStatus;
import microservice.inventory_service.internal.inventory.core.alert.domain.entity.valueobject.AlertType;
import microservice.inventory_service.internal.inventory.core.alert.domain.entity.valueobject.AlertId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface InventoryAlertRepository {
    InventoryAlert save(InventoryAlert alert);

    Optional<InventoryAlert> findById(AlertId id);

    Page<InventoryAlert> findByInventoryId(AlertId inventoryId);

    Page<InventoryAlert> findByType(AlertType type);

    Page<InventoryAlert> findByStatus(AlertStatus status, Pageable pageable);

    Page<InventoryAlert> findBySeverity(AlertSeverity severity);

    Page<InventoryAlert> findActiveAlerts();

    void delete(AlertId id);
}
