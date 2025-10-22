package microservice.inventory.domain.port.output;

import microservice.inventory.domain.entity.InventoryMovement;
import microservice.inventory.domain.entity.enums.MovementType;
import microservice.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory.domain.entity.valueobject.id.MovementId;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InventoryMovementRepository {
    InventoryMovement save(InventoryMovement movement);
    void bulkSave(Iterable<InventoryMovement> movements);

    Optional<InventoryMovement> findByID(MovementId id);

    Page<InventoryMovement> findByInventoryID(InventoryId inventoryId);

    Page<InventoryMovement> findByBatchID(BatchId batchId);

    Page<InventoryMovement> findByMovementType(MovementType type);

    Page<InventoryMovement> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<InventoryMovement> findByInventoryIdAndDateRange(InventoryId inventoryId, LocalDateTime startDate, LocalDateTime endDate);
}