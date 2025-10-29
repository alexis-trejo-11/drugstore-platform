package microservice.inventory_service.inventory.adapter.outbound.persistence.repository;

import libs_kernel.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.movement.domain.InventoryMovement;
import microservice.inventory_service.inventory.core.movement.domain.port.InventoryMovementRepository;
import microservice.inventory_service.inventory.core.movement.domain.valueobject.MovementType;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.movement.domain.valueobject.MovementId;
import microservice.inventory_service.inventory.adapter.outbound.persistence.model.InventoryMovementEntity;
import microservice.inventory_service.inventory.adapter.outbound.persistence.repository.jpa.JpaInventoryMovementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InventoryMovementRepositoryImpl implements InventoryMovementRepository {
    private JpaInventoryMovementRepository movementRepository;
    private EntityMapper<InventoryMovementEntity, InventoryMovement> movementMapper;

    @Override
    public InventoryMovement save(InventoryMovement movement) {
        InventoryMovementEntity entity = movementMapper.fromDomain(movement);
        InventoryMovementEntity savedEntity = movementRepository.save(entity);
        return movementMapper.toDomain(savedEntity);
    }

    @Override
    public void bulkSave(List<InventoryMovement> movements) {
        List<InventoryMovementEntity> entities = movementMapper.fromDomains(movements);
        movementRepository.saveAll(entities);
    }

    @Override
    public Optional<InventoryMovement> findById(MovementId id) {
        return movementRepository.findById(id.value())
                .map(movementMapper::toDomain);
    }

    @Override
    public Page<InventoryMovement> findByInventoryId(InventoryId inventoryId, Pageable pageable) {
        Page<InventoryMovementEntity> entityPage = movementRepository.findByInventoryId(inventoryId.value(), pageable);
        return movementMapper.toDomainPage(entityPage);
    }

    @Override
    public Page<InventoryMovement> findByBatchId(BatchId batchId, Pageable pageable) {
        Page<InventoryMovementEntity> entityPage = movementRepository.findByBatchId(batchId.value(), pageable);
        return movementMapper.toDomainPage(entityPage);
    }

    @Override
    public Page<InventoryMovement> findByMovementType(MovementType type, Pageable pageable) {
        Page<InventoryMovementEntity> entityPage = movementRepository.findByMovementType(type.name(), pageable);
        return movementMapper.toDomainPage(entityPage);
    }

    @Override
    public Page<InventoryMovement> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<InventoryMovementEntity> entityPage = movementRepository.findByDateBetween(startDate, endDate, pageable);
        return movementMapper.toDomainPage(entityPage);
    }

    @Override
    public Page<InventoryMovement> findByInventoryIdAndDateRange(InventoryId inventoryId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<InventoryMovementEntity> entityPage = movementRepository.findAllByInventoryIdAndDateBetween(inventoryId.value(), startDate, endDate, pageable);
        return movementMapper.toDomainPage(entityPage);
    }
}
