package microservice.inventory_service.internal.core.movement.domain.port;

import microservice.inventory_service.internal.core.movement.domain.InventoryMovement;
import microservice.inventory_service.internal.core.movement.domain.valueobject.MovementType;
import microservice.inventory_service.internal.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.internal.core.movement.domain.valueobject.MovementId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InventoryMovementOutputPort {
    InventoryMovement save(InventoryMovement movement);
    void bulkSave(List<InventoryMovement> movements);

    Optional<InventoryMovement> findById(MovementId id);

    Page<InventoryMovement> findByInventoryId(InventoryId inventoryId, Pageable pageable);

    Page<InventoryMovement> findByBatchId(BatchId batchId, Pageable pageable);

    Page<InventoryMovement> findByMovementType(MovementType type, Pageable pageable);

    Page<InventoryMovement> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<InventoryMovement> findByInventoryIdAndDateRange(InventoryId inventoryId, LocalDateTime startDate, LocalDateTime endDate,  Pageable pageable);
}