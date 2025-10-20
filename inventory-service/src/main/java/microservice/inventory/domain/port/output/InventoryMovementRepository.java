package microservice.inventory.domain.port.output;

import microservice.inventory.domain.entity.InventoryMovement;
import microservice.inventory.domain.entity.enums.MovementType;
import microservice.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory.domain.entity.valueobject.id.InventoryID;
import microservice.inventory.domain.entity.valueobject.id.MovementID;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;

public interface InventoryMovementRepository {
    InventoryMovement save(InventoryMovement movement);

    Optional<InventoryMovement> findByID(MovementID id);

    Page<InventoryMovement> findByInventoryID(InventoryID inventoryId);

    Page<InventoryMovement> findByBatchID(BatchId batchId);

    Page<InventoryMovement> findByMovementType(MovementType type);

    Page<InventoryMovement> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Page<InventoryMovement> findByInventoryIdAndDateRange(InventoryID inventoryId, LocalDateTime startDate, LocalDateTime endDate);
}