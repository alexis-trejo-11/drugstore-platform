package microservice.inventory_service.inventory.adapter.outbound.persistence.repository;

import libs_kernel.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchStatus;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.batch.port.output.InventoryBatchRepository;
import microservice.inventory_service.inventory.adapter.outbound.persistence.model.InventoryBatchEntity;
import microservice.inventory_service.inventory.adapter.outbound.persistence.repository.jpa.JpaInventoryBatchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<InventoryBatch> findByInventoryId(InventoryId inventoryId, Pageable pageable, boolean activeOnly) {
        Page<InventoryBatchEntity> batches = jpaRepository.findByInventoryIdAndStatus(inventoryId.value(), pageable, BatchStatus.ACTIVE);
        return entityMapper.toDomainPage(batches);
    }

    @Override
    public List<InventoryBatch> findByInventoryId(InventoryId inventoryId, boolean activeOnly) {
        List<InventoryBatchEntity> entities = jpaRepository.findByInventoryIdAndStatus(inventoryId.value(), activeOnly ? BatchStatus.ACTIVE : null);
        return entityMapper.toDomains(entities);
    }

    @Override
    public Page<InventoryBatch> findByStatus(BatchStatus status, Pageable pageable) {
        Page<InventoryBatchEntity> batches = jpaRepository.findByStatus(status, pageable);
        return entityMapper.toDomainPage(batches);
    }

    @Override
    public Page<InventoryBatch> findExpiringBefore(LocalDateTime date, Pageable pageable) {
        Page<InventoryBatchEntity> batches = jpaRepository.findByExpirationDateBefore(date, pageable);
        return entityMapper.toDomainPage(batches);
    }

    @Override
    public List<InventoryBatch> findExpiringBefore(LocalDateTime date) {
        return entityMapper.toDomains(jpaRepository.findByExpirationDateBefore(date));
    }

    @Override
    public Page<InventoryBatch> findExpiredBatches(Pageable pageable) {
        Page<InventoryBatchEntity> batches = jpaRepository.findByStatus(BatchStatus.EXPIRED, pageable);
        return entityMapper.toDomainPage(batches);
    }

    @Override
    public List<InventoryBatch> findExpiredBatches() {
        List<InventoryBatchEntity> entities = jpaRepository.findActive(BatchStatus.EXPIRED);
        return entityMapper.toDomains(entities);
    }

    @Override
    public void delete(BatchId id) {
        jpaRepository.deleteById(id.value());
    }
}
