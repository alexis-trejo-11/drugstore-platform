package microservice.inventory_service.inventory.infrastructure.adapter.outbound.persistence.repository;

import libs_kernel.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.domain.entity.enums.BatchStatus;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory_service.inventory.domain.port.output.InventoryBatchRepository;
import microservice.inventory_service.inventory.infrastructure.adapter.outbound.persistence.model.InventoryBatchEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InventoryBatchRepositoryImpl implements InventoryBatchRepository {
    private final JpaInventoryBatchRepository jpaRepository;
    private final EntityMapper<InventoryBatchEntity, InventoryBatch> entityMapper;

    @Override
    public InventoryBatch save(InventoryBatch batch) {
        InventoryBatchEntity entity = entityMapper.fromDomain(batch);
        InventoryBatchEntity savedEntity = jpaRepository.save(entity);
        return entityMapper.toDomain(savedEntity);
    }

    @Override
    public void bulkSave(List<InventoryBatch> batches) {
        List<InventoryBatchEntity> entities = batches.stream()
                .map(entityMapper::fromDomain)
                .toList();

        jpaRepository.saveAll(entities);
    }

    @Override
    public Optional<InventoryBatch> findById(BatchId id) {
        Optional<InventoryBatchEntity> entityOpt = jpaRepository.findById(id.value());
        return entityOpt.map(entityMapper::toDomain);
    }

    @Override
    public List<InventoryBatch> findByInventoryId(InventoryId inventoryId) {
        List<InventoryBatchEntity> batches = jpaRepository.findByInventoryId(inventoryId.value());
        return entityMapper.toDomains(batches);
    }

    @Override
    public List<InventoryBatch> findByStatus(BatchStatus status) {
        List<InventoryBatchEntity> batches = jpaRepository.findByStatus(status);
        return entityMapper.toDomains(batches);
    }

    @Override
    public List<InventoryBatch> findExpiringBefore(LocalDateTime date) {
        List<InventoryBatchEntity> batches = jpaRepository.findByExpirationDateBefore(date);
        return entityMapper.toDomains(batches);
    }

    @Override
    public List<InventoryBatch> findExpiredBatches() {
        List<InventoryBatchEntity> batches = jpaRepository.findExpiredBatches();
        return entityMapper.toDomains(batches);
    }

    @Override
    public Optional<InventoryBatch> findByBatchNumber(String batchNumber) {
        Optional<InventoryBatchEntity> entityOpt = jpaRepository.findByBatchNumber(batchNumber);
        return entityOpt.map(entityMapper::toDomain);
    }

    @Override
    public List<InventoryBatch> findAvailableBatchesByInventoryId(InventoryId inventoryId) {
        List<InventoryBatchEntity> batches = jpaRepository.findActiveByInventoryId(inventoryId.value());
        return entityMapper.toDomains(batches);
    }

    @Override
    public void delete(BatchId id) {
        jpaRepository.deleteById(id.value());
    }
}
