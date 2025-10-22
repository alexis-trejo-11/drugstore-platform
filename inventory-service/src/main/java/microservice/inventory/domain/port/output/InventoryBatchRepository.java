package microservice.inventory.domain.port.output;

import microservice.inventory.domain.entity.InventoryBatch;
import microservice.inventory.domain.entity.enums.BatchStatus;
import microservice.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;

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
