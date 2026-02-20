package io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.inbound.api.rest.mapper;

import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.InventoryBatch;
import io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.inbound.api.rest.dto.response.BatchResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BatchResponseMapper implements ResponseMapper<BatchResponse, InventoryBatch> {
    @Override
    public BatchResponse toResponse(InventoryBatch inventoryBatch) {
        if (inventoryBatch == null) return null;

        return BatchResponse.builder()
                .id(inventoryBatch.getId().value())
                .batchNumber(inventoryBatch.getBatchNumber())
                .lotNumber(inventoryBatch.getLotNumber())
                .quantity(inventoryBatch.getQuantity())
                .availableQuantity(inventoryBatch.getAvailableQuantity())
                .manufacturingDate(inventoryBatch.getManufacturingDate())
                .expirationDate(inventoryBatch.getExpirationDate())
                .supplierId(inventoryBatch.getSupplierId())
                .supplierName(inventoryBatch.getSupplierName())
                .status(inventoryBatch.getStatus())
                .storageConditions(inventoryBatch.getStorageConditions())
                .isExpired(inventoryBatch.isExpired())
                .isExpiringSoon(inventoryBatch.isExpiringSoon(30))
                .receivedDate(inventoryBatch.getReceivedDate())
                .createdAt(inventoryBatch.getCreatedAt())
                .build();
    }

    @Override
    public List<BatchResponse> toResponses(List<InventoryBatch> inventoryBatches) {
        if (inventoryBatches == null) return null;

        return inventoryBatches.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PageResponse<BatchResponse> toResponsePage(Page<InventoryBatch> inventoryBatches) {
        if (inventoryBatches == null) return null;
        if (!inventoryBatches.hasContent()) {
            return PageResponse.empty();
        }

        Page<BatchResponse> batchResponses = inventoryBatches.map(this::toResponse);
        return PageResponse.from(batchResponses);
    }
}
