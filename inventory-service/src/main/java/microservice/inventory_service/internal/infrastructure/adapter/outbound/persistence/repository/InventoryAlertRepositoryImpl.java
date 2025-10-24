package microservice.inventory_service.internal.infrastructure.adapter.outbound.persistence.repository;

import microservice.inventory_service.internal.core.alert.domain.entity.InventoryAlert;
import microservice.inventory_service.internal.core.alert.domain.entity.valueobject.AlertSeverity;
import microservice.inventory_service.internal.core.alert.domain.entity.valueobject.AlertStatus;
import microservice.inventory_service.internal.core.alert.domain.entity.valueobject.AlertType;
import microservice.inventory_service.internal.core.alert.domain.entity.valueobject.AlertId;
import microservice.inventory_service.internal.core.alert.domain.port.InventoryAlertRepository;
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
