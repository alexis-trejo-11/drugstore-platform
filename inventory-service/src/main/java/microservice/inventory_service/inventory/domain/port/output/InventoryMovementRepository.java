package microservice.inventory_service.inventory.domain.port.output;

import microservice.inventory_service.inventory.domain.entity.InventoryMovement;
import microservice.inventory_service.inventory.domain.entity.enums.MovementType;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.MovementId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InventoryMovementRepository {
    InventoryMovement save(InventoryMovement movement);
    void bulkSave(List<InventoryMovement> movements);

    Optional<InventoryMovement> findById(MovementId id);

    Page<InventoryMovement> findByInventoryId(InventoryId inventoryId, Pageable pageable);

    Page<InventoryMovement> findByBatchId(BatchId batchId, Pageable pageable);

    Page<InventoryMovement> findByMovementType(MovementType type, Pageable pageable);

    Page<InventoryMovement> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<InventoryMovement> findByInventoryIdAndDateRange(InventoryId inventoryId, LocalDateTime startDate, LocalDateTime endDate);
}