package io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.port;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.entity.InventoryAlert;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.entity.valueobject.AlertSeverity;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.entity.valueobject.AlertStatus;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.entity.valueobject.AlertType;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.entity.valueobject.AlertId;
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
