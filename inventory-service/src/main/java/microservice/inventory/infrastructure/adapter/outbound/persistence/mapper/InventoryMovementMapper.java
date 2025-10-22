package microservice.inventory.infrastructure.adapter.outbound.persistence.mapper;

import libs_kernel.mapper.EntityMapper;
import microservice.inventory.domain.entity.InventoryMovement;
import microservice.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory.domain.entity.valueobject.id.MovementId;
import microservice.inventory.domain.entity.valueobject.id.UserId;
import microservice.inventory.infrastructure.adapter.outbound.persistence.model.InventoryMovementEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public class InventoryMovementMapper implements EntityMapper<InventoryMovementEntity, InventoryMovement> {
    @Override
    public InventoryMovementEntity fromDomains(InventoryMovement inventoryMovement) {
        if (inventoryMovement == null) {
            return null;
        }

        return InventoryMovementEntity.builder()
                .id(inventoryMovement.getId() != null ? inventoryMovement.getId().value() : null)
                .inventoryId(inventoryMovement.getInventoryId() != null ? inventoryMovement.getInventoryId().value() : null)
                .batchId(inventoryMovement.getBatchId()  != null ? inventoryMovement.getBatchId().value() : null)
                .movementType(inventoryMovement.getMovementType())
                .newQuantity(inventoryMovement.getNewQuantity())
                .previousQuantity(inventoryMovement.getPreviousQuantity())
                .newQuantity(inventoryMovement.getNewQuantity())
                .reason(inventoryMovement.getReason())
                .referenceId(inventoryMovement.getReferenceId())
                .referenceType(inventoryMovement.getReferenceType())
                .performedBy(inventoryMovement.getPerformedBy() != null ? inventoryMovement.getPerformedBy().value() : null)
                .notes(inventoryMovement.getNotes())
                .movementDate(inventoryMovement.getMovementDate())
                .createdAt(inventoryMovement.getCreatedAt())
                .build();
    }

    @Override
    public InventoryMovement toDomain(InventoryMovementEntity model) {
        if (model == null) {
            return null;
        }
        return InventoryMovement.builder()
                .id(model.getId() != null ? new MovementId(model.getId()) : null)
                .inventoryId(model.getInventoryId() != null ? new InventoryId(model.getInventoryId()) : null)
                .batchId(model.getBatchId() != null ? new BatchId(model.getBatchId()) : null)
                .movementType(model.getMovementType())
                .quantity(model.getQuantity())
                .previousQuantity(model.getPreviousQuantity())
                .newQuantity(model.getNewQuantity())
                .reason(model.getReason())
                .referenceId(model.getReferenceId())
                .referenceType(model.getReferenceType())
                .performedBy(model.getPerformedBy() != null ? new UserId(model.getPerformedBy()) : null)
                .notes(model.getNotes())
                .movementDate(model.getMovementDate())
                .createdAt(model.getCreatedAt())
                .build();
    }

    @Override
    public List<InventoryMovementEntity> fromDomains(List<InventoryMovement> inventoryMovements) {
        if (inventoryMovements == null || inventoryMovements.isEmpty()) {
            return List.of();
        }
        return inventoryMovements.stream()
                .map(this::fromDomains)
                .toList();
    }

    @Override
    public List<InventoryMovement> toDomain(List<InventoryMovementEntity> inventoryMovementEntities) {
        if (inventoryMovementEntities == null || inventoryMovementEntities.isEmpty()) {
            return List.of();
        }
        return inventoryMovementEntities.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Page<InventoryMovement> toDomainPage(Page<InventoryMovementEntity> modelPage) {
        return modelPage.map(this::toDomain);
    }
}
