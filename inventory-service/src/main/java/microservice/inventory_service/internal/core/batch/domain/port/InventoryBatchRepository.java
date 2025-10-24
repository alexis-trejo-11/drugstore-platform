package microservice.inventory_service.internal.core.batch.domain.port;

import microservice.inventory_service.internal.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.internal.core.batch.domain.entity.valueobject.BatchStatus;
import microservice.inventory_service.internal.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.InventoryId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InventoryBatchRepository {
    InventoryBatch save(InventoryBatch batch);
    void bulkSave(List<InventoryBatch> batches);

    Optional<InventoryBatch> findById(BatchId id);

    List<InventoryBatch> findByInventoryId(InventoryId inventoryId);

    List<InventoryBatch> findByStatus(BatchStatus status);

    List<InventoryBatch> findExpiringBefore(LocalDateTime date);

    List<InventoryBatch> findExpiredBatches();

    Optional<InventoryBatch> findByBatchNumber(String batchNumber);

    List<InventoryBatch> findAvailableBatchesByInventoryId(InventoryId inventoryId);

    void delete(BatchId id);
}
