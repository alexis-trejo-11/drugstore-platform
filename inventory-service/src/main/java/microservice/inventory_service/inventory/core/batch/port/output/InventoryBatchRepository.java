package microservice.inventory_service.inventory.core.batch.port.output;

import microservice.inventory_service.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchStatus;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.ProductId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InventoryBatchRepository {
    InventoryBatch save(InventoryBatch batch);

    void bulkSave(List<InventoryBatch> batches);

    Optional<InventoryBatch> findById(BatchId id);

    Page<InventoryBatch> findByInventoryId(InventoryId inventoryId, Pageable pageable, boolean activeOnly);
    List<InventoryBatch> findByInventoryId(InventoryId inventoryId, boolean activeOnly);


    Page<InventoryBatch> findByStatus(BatchStatus status, Pageable pageable);

    Page<InventoryBatch> findExpiringBefore(LocalDateTime date, Pageable pageable);
    List<InventoryBatch> findExpiringBefore(LocalDateTime date);

    Page<InventoryBatch> findExpiredBatches(Pageable pageable);
    List<InventoryBatch> findExpiredBatches();

    void delete(BatchId id);
}
