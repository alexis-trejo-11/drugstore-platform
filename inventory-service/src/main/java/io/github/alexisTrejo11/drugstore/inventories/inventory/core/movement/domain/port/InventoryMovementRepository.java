package io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.port;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.InventoryMovement;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.valueobject.MovementType;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.valueobject.BatchId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.valueobject.MovementId;
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

    Page<InventoryMovement> findByInventoryIdAndDateRange(InventoryId inventoryId, LocalDateTime startDate, LocalDateTime endDate,  Pageable pageable);
}