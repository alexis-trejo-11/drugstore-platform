package microservice.inventory.infrastructure.adapter.outbound.persistence.repository;

import microservice.inventory.domain.entity.InventoryMovement;
import microservice.inventory.domain.entity.enums.MovementType;
import microservice.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory.domain.entity.valueobject.id.MovementId;
import microservice.inventory.domain.port.output.InventoryMovementRepository;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;

public class InventoryMovementRepositoryImpl implements InventoryMovementRepository {
    @Override
    public InventoryMovement save(InventoryMovement movement) {
        return null;
    }

    @Override
    public Optional<InventoryMovement> findByID(MovementId id) {
        return Optional.empty();
    }

    @Override
    public Page<InventoryMovement> findByInventoryID(InventoryId inventoryId) {
        return null;
    }

    @Override
    public Page<InventoryMovement> findByBatchID(BatchId batchId) {
        return null;
    }

    @Override
    public Page<InventoryMovement> findByMovementType(MovementType type) {
        return null;
    }

    @Override
    public Page<InventoryMovement> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return null;
    }

    @Override
    public Page<InventoryMovement> findByInventoryIdAndDateRange(InventoryId inventoryId, LocalDateTime startDate, LocalDateTime endDate) {
        return null;
    }
}
