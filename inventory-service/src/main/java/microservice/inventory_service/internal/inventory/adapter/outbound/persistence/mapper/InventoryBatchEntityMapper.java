package microservice.inventory_service.internal.inventory.adapter.outbound.persistence.mapper;

import libs_kernel.mapper.EntityMapper;
import microservice.inventory_service.internal.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.internal.inventory.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.internal.inventory.adapter.outbound.persistence.model.InventoryBatchEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryBatchEntityMapper implements EntityMapper<InventoryBatchEntity, InventoryBatch> {
    @Override
    public InventoryBatchEntity fromDomain(InventoryBatch batch) {
        if (batch == null) return null;

        return InventoryBatchEntity.builder()
                .id(batch.getId() != null ? batch.getId().value() : null)
                .inventoryId(batch.getInventoryId() != null ? batch.getInventoryId().value() : null)
                .batchNumber(batch.getBatchNumber())
                .lotNumber(batch.getLotNumber())
                .quantity(batch.getQuantity())
                .availableQuantity(batch.getAvailableQuantity())
                .costPerUnit(batch.getCostPerUnit())
                .manufacturingDate(batch.getManufacturingDate())
                .expirationDate(batch.getExpirationDate())
                .supplierId(batch.getSupplierId())
                .supplierName(batch.getSupplierName())
                .status(batch.getStatus())
                .storageConditions(batch.getStorageConditions())
                .receivedDate(batch.getReceivedDate())
                .createdAt(batch.getCreatedAt())
                .updatedAt(batch.getUpdatedAt())
                .build();
    }

    @Override
    public InventoryBatch toDomain(InventoryBatchEntity model) {
        if (model == null) return null;

        return InventoryBatch.reconstructor()
                .id(model.getId() != null ? new BatchId(model.getId()) : null)
                .inventoryId(model.getInventoryId() != null ? new InventoryId(model.getInventoryId()) : null)
                .batchNumber(model.getBatchNumber())
                .lotNumber(model.getLotNumber())
                .quantity(model.getQuantity())
                .availableQuantity(model.getAvailableQuantity())
                .costPerUnit(model.getCostPerUnit())
                .manufacturingDate(model.getManufacturingDate())
                .expirationDate(model.getExpirationDate())
                .supplierId(model.getSupplierId())
                .supplierName(model.getSupplierName())
                .status(model.getStatus())
                .storageConditions(model.getStorageConditions())
                .receivedDate(model.getReceivedDate())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .reconstruct();
    }

    @Override
    public List<InventoryBatchEntity> fromDomains(List<InventoryBatch> batches) {
        if (batches == null) return List.of();
        return batches.stream()
                .map(this::fromDomain)
                .toList();
    }

    @Override
    public List<InventoryBatch> toDomains(List<InventoryBatchEntity> inventoryBatchEntities) {
        if (inventoryBatchEntities == null) return List.of();

        return inventoryBatchEntities.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Page<InventoryBatch> toDomainPage(Page<InventoryBatchEntity> modelPage) {
        if (modelPage == null) return Page.empty();

        return modelPage.map(this::toDomain);
    }
}
