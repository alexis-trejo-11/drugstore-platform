package microservice.inventory_service.internal.infrastructure.adapter.outbound.persistence.mapper;

import libs_kernel.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.internal.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.internal.core.movement.domain.InventoryMovement;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.MedicineId;
import microservice.inventory_service.internal.infrastructure.adapter.outbound.persistence.model.InventoryBatchEntity;
import microservice.inventory_service.internal.infrastructure.adapter.outbound.persistence.model.InventoryEntity;
import microservice.inventory_service.internal.infrastructure.adapter.outbound.persistence.model.InventoryMovementEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InventoryEntityMapper implements EntityMapper<InventoryEntity, Inventory> {
    private final EntityMapper<InventoryBatchEntity, InventoryBatch> batchMapper;
    private final EntityMapper<InventoryMovementEntity, InventoryMovement> movementMapper;

    @Override
    public InventoryEntity fromDomain(Inventory inventory) {
        if (inventory == null) return null;

        return InventoryEntity.builder()
                .id(inventory.getId() != null ? inventory.getId().value() : null)
                .medicineId(inventory.getMedicineId() != null ? inventory.getMedicineId().value() : null)
                .totalQuantity(inventory.getTotalQuantity())
                .availableQuantity(inventory.getAvailableQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .reorderLevel(inventory.getReorderLevel())
                .reorderQuantity(inventory.getReorderQuantity())
                .maximumStockLevel(inventory.getMaximumStockLevel())
                .warehouseLocation(inventory.getWarehouseLocation())
                .status(inventory.getStatus())
                .lastRestockedDate(inventory.getLastRestockedDate())
                .lastCountDate(inventory.getLastCountDate())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .batches(batchMapper.fromDomains(inventory.getBatches()))
                .movements(movementMapper.fromDomains(inventory.getMovements()))
                .build();
    }

    @Override
    public Inventory toDomain(InventoryEntity model) {
        if (model == null) return null;

        return Inventory.reconstructor()
                .id(model.getId() != null ? new InventoryId(model.getId()) : null)
                .medicineId(model.getMedicineId() != null ? new MedicineId(model.getMedicineId()) : null)
                .totalQuantity(model.getTotalQuantity())
                .availableQuantity(model.getAvailableQuantity())
                .reservedQuantity(model.getReservedQuantity())
                .reorderLevel(model.getReorderLevel())
                .reorderQuantity(model.getReorderQuantity())
                .maximumStockLevel(model.getMaximumStockLevel())
                .warehouseLocation(model.getWarehouseLocation())
                .status(model.getStatus())
                .lastRestockedDate(model.getLastRestockedDate())
                .lastCountDate(model.getLastCountDate())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .batches(batchMapper.toDomains(model.getBatches()))
                .movements(movementMapper.toDomains(model.getMovements()))
                .reconstruct();

    }

    @Override
    public List<InventoryEntity> fromDomains(List<Inventory> inventories) {
        if (inventories == null) return List.of();
        return inventories.stream()
                .map(this::fromDomain)
                .toList();
    }

    @Override
    public List<Inventory> toDomains(List<InventoryEntity> inventoryEntities) {
        if (inventoryEntities == null) return List.of();
        return inventoryEntities.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Page<Inventory> toDomainPage(Page<InventoryEntity> modelPage) {
        if (modelPage == null) return Page.empty();
        return modelPage.map(this::toDomain);
    }
}
