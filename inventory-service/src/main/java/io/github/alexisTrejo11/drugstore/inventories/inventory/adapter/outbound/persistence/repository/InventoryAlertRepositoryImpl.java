package io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.outbound.persistence.repository;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.entity.InventoryAlert;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.entity.valueobject.AlertSeverity;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.entity.valueobject.AlertStatus;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.entity.valueobject.AlertType;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.entity.valueobject.AlertId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.port.InventoryAlertRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InventoryAlertRepositoryImpl implements InventoryAlertRepository {
    @Override
    public InventoryAlert save(InventoryAlert alert) {
        return null;
    }

    @Override
    public Optional<InventoryAlert> findById(AlertId id) {
        return Optional.empty();
    }

    @Override
    public Page<InventoryAlert> findByInventoryId(AlertId inventoryId) {
        return null;
    }

    @Override
    public Page<InventoryAlert> findByType(AlertType type) {
        return null;
    }

    @Override
    public Page<InventoryAlert> findByStatus(AlertStatus status, Pageable pageable) {
        return null;
    }

    @Override
    public Page<InventoryAlert> findBySeverity(AlertSeverity severity) {
        return null;
    }

    @Override
    public Page<InventoryAlert> findActiveAlerts() {
        return null;
    }

    @Override
    public void delete(AlertId id) {

    }
}
