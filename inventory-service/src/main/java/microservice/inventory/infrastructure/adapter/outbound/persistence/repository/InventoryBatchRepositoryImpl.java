package microservice.inventory.infrastructure.adapter.outbound.persistence.repository;

import microservice.inventory.domain.entity.enums.BatchStatus;
import microservice.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory.domain.port.output.InventoryBatchRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class InventoryBatchRepositoryImpl implements InventoryBatchRepository {

    @Override
    public InventoryBatch save(InventoryBatch batch) {
        return null;
    }

    @Override
    public Optional<InventoryBatch> findById(BatchId id) {
        return Optional.empty();
    }

    @Override
    public List<InventoryBatch> findByInventoryId(InventoryId inventoryId) {
        return List.of();
    }

    @Override
    public List<InventoryBatch> findByStatus(BatchStatus status) {
        return List.of();
    }

    @Override
    public List<InventoryBatch> findExpiringBefore(LocalDateTime date) {
        return List.of();
    }

    @Override
    public List<InventoryBatch> findExpiredBatches() {
        return List.of();
    }

    @Override
    public Optional<InventoryBatch> findByBatchNumber(String batchNumber) {
        return Optional.empty();
    }

    @Override
    public List<InventoryBatch> findAvailableBatchesByInventoryId(InventoryId inventoryId) {
        return List.of();
    }

    @Override
    public void delete(BatchId id) {

    }
}
