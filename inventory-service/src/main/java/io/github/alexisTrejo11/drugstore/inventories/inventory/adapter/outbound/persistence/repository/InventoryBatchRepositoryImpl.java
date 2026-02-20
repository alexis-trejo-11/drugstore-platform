package io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.outbound.persistence.repository;

import libs_kernel.mapper.JpaEntityMapper;
import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.InventoryBatch;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.valueobject.BatchStatus;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.valueobject.BatchId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.port.output.InventoryBatchRepository;
import io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.outbound.persistence.model.InventoryBatchEntity;
import io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.outbound.persistence.repository.jpa.JpaInventoryBatchRepository;
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
    private final JpaEntityMapper<InventoryBatchEntity, InventoryBatch> jpaEntityMapper;

    @Override
    public InventoryBatch save(InventoryBatch batch) {
        InventoryBatchEntity entity = jpaEntityMapper.fromDomain(batch);
        InventoryBatchEntity savedEntity = jpaRepository.save(entity);
        return jpaEntityMapper.toDomain(savedEntity);
    }

    @Override
    public void bulkSave(List<InventoryBatch> batches) {
        List<InventoryBatchEntity> entities = batches.stream()
                .map(jpaEntityMapper::fromDomain)
                .toList();

        jpaRepository.saveAll(entities);
    }

    @Override
    public Optional<InventoryBatch> findById(BatchId id) {
        Optional<InventoryBatchEntity> entityOpt = jpaRepository.findById(id.value());
        return entityOpt.map(jpaEntityMapper::toDomain);
    }

    @Override
    public Page<InventoryBatch> findByInventoryId(InventoryId inventoryId, Pageable pageable, boolean activeOnly) {
        Page<InventoryBatchEntity> batches = jpaRepository.findByInventoryIdAndStatus(inventoryId.value(), pageable, BatchStatus.ACTIVE);
        return jpaEntityMapper.toDomainPage(batches);
    }

    @Override
    public List<InventoryBatch> findByInventoryId(InventoryId inventoryId, boolean activeOnly) {
        List<InventoryBatchEntity> entities = jpaRepository.findByInventoryIdAndStatus(inventoryId.value(), activeOnly ? BatchStatus.ACTIVE : null);
        return jpaEntityMapper.toDomains(entities);
    }

    @Override
    public Page<InventoryBatch> findByStatus(BatchStatus status, Pageable pageable) {
        Page<InventoryBatchEntity> batches = jpaRepository.findByStatus(status, pageable);
        return jpaEntityMapper.toDomainPage(batches);
    }

    @Override
    public Page<InventoryBatch> findExpiringBefore(LocalDateTime date, Pageable pageable) {
        Page<InventoryBatchEntity> batches = jpaRepository.findByExpirationDateBefore(date, pageable);
        return jpaEntityMapper.toDomainPage(batches);
    }

    @Override
    public List<InventoryBatch> findExpiringBefore(LocalDateTime date) {
        return jpaEntityMapper.toDomains(jpaRepository.findByExpirationDateBefore(date));
    }

    @Override
    public Page<InventoryBatch> findExpiredBatches(Pageable pageable) {
        Page<InventoryBatchEntity> batches = jpaRepository.findByStatus(BatchStatus.EXPIRED, pageable);
        return jpaEntityMapper.toDomainPage(batches);
    }

    @Override
    public List<InventoryBatch> findExpiredBatches() {
        List<InventoryBatchEntity> entities = jpaRepository.findActive(BatchStatus.EXPIRED);
        return jpaEntityMapper.toDomains(entities);
    }

    @Override
    public void delete(BatchId id) {
        jpaRepository.deleteById(id.value());
    }
}
