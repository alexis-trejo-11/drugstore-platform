package microservice.inventory_service.inventory.adapter.outbound.persistence.mapper;

import libs_kernel.mapper.JpaEntityMapper;
import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.core.movement.domain.InventoryMovement;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.ProductId;
import microservice.inventory_service.inventory.adapter.outbound.persistence.model.InventoryBatchEntity;
import microservice.inventory_service.inventory.adapter.outbound.persistence.model.InventoryEntity;
import microservice.inventory_service.inventory.adapter.outbound.persistence.model.InventoryMovementEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InventoryJpaEntityMapper implements JpaEntityMapper<InventoryEntity, Inventory> {
    private final JpaEntityMapper<InventoryBatchEntity, InventoryBatch> batchMapper;
    private final JpaEntityMapper<InventoryMovementEntity, InventoryMovement> movementMapper;

    @Override
    public InventoryEntity fromDomain(Inventory inventory) {
        if (inventory == null) return null;

        return InventoryEntity.builder()
                .id(inventory.getId() != null ? inventory.getId().value() : null)
                .productId(inventory.getProductId() != null ? inventory.getProductId().value() : null)
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
                .productId(model.getProductId() != null ? new ProductId(model.getProductId()) : null)
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
