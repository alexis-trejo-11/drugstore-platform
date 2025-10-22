package microservice.inventory_service.inventory.infrastructure.adapter.outbound.persistence.repository;

import microservice.inventory_service.inventory.domain.entity.InventoryAlert;
import microservice.inventory_service.inventory.domain.entity.enums.AlertSeverity;
import microservice.inventory_service.inventory.domain.entity.enums.AlertStatus;
import microservice.inventory_service.inventory.domain.entity.enums.AlertType;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.AlertId;
import microservice.inventory_service.inventory.domain.port.output.InventoryAlertRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InventoryAlerRepositoryImpl implements InventoryAlertRepository  {
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
    public Page<InventoryAlert> findByStatus(AlertStatus status) {
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
