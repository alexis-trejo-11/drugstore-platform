package microservice.inventory.domain.port.output;

import microservice.inventory.domain.entity.InventoryBatch;
import microservice.inventory.domain.entity.enums.BatchStatus;
import microservice.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory.domain.entity.valueobject.id.InventoryID;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InventoryBatchRepository {
    InventoryBatch save(InventoryBatch batch);

    Optional<InventoryBatch> findByID(BatchId id);

    List<InventoryBatch> findByInventoryId(InventoryID inventoryId);

    List<InventoryBatch> findByStatus(BatchStatus status);

    List<InventoryBatch> findExpiringBefore(LocalDateTime date);

    List<InventoryBatch> findExpiredBatches();

    Optional<InventoryBatch> findByBatchNumber(String batchNumber);

    List<InventoryBatch> findAvailableBatchesByInventoryId(InventoryID inventoryId);

    void delete(BatchId id);
}
