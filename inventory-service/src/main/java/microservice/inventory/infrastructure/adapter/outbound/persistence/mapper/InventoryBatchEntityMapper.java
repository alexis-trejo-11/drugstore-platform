package microservice.inventory.infrastructure.adapter.outbound.persistence.mapper;

import libs_kernel.mapper.EntityMapper;
import microservice.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory.infrastructure.adapter.outbound.persistence.model.InventoryBatchEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public class InventoryBatchEntityMapper implements EntityMapper<InventoryBatchEntity, InventoryBatch> {
    @Override
    public InventoryBatchEntity fromDomains(InventoryBatch batch) {
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

        return InventoryBatch.builder()
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
                .build();
    }

    @Override
    public List<InventoryBatchEntity> fromDomains(List<InventoryBatch> batches) {
        if (batches == null || batches.isEmpty()) return List.of();
        return batches.stream().map(this::fromDomains).toList();
    }

    @Override
    public List<InventoryBatch> toDomain(List<InventoryBatchEntity> inventoryBatchEntities) {
        if (inventoryBatchEntities == null || inventoryBatchEntities.isEmpty()) return List.of();
        return inventoryBatchEntities.stream().map(this::toDomain).toList();
    }

    @Override
    public Page<InventoryBatch> toDomainPage(Page<InventoryBatchEntity> modelPage) {
        if (modelPage == null || modelPage.isEmpty()) return Page.empty();
        return modelPage.map(this::toDomain);
    }
}
