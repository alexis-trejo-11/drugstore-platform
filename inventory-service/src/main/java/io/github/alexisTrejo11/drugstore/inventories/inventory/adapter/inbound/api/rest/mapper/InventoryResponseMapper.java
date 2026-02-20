package io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.inbound.api.rest.mapper;

import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.Inventory;
import io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.inbound.api.rest.dto.response.InventoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryResponseMapper implements ResponseMapper<InventoryResponse, Inventory> {
    @Override
    public InventoryResponse toResponse(Inventory inventory) {
        if (inventory == null) {
            return null;
        }

        return InventoryResponse.builder()
                .id(inventory.getId() != null ? inventory.getId().value() : null)
                .productId(inventory.getProductId() != null ? inventory.getProductId().value() : null)
                .totalQuantity(inventory.getTotalQuantity())
                .availableQuantity(inventory.getAvailableQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .reorderQuantity(inventory.getReorderQuantity())
                .maximumStockLevel(inventory.getMaximumStockLevel())
                .lastRestockedDate(inventory.getLastRestockedDate())
                .lastCountDate(inventory.getLastCountDate())
                //.batchesCount(inventory.getBatchesCount())
                //.movementsCount(inventory.getMovementsCount())
                .createdAt(inventory.getCreatedAt())
                .status(inventory.getStatus())
                .warehouseLocation(inventory.getWarehouseLocation())
                .reorderLevel(inventory.getReorderLevel())
                .build();
    }

    @Override
    public List<InventoryResponse> toResponses(List<Inventory> inventories) {
        return inventories.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PageResponse<InventoryResponse> toResponsePage(Page<Inventory> dtoPage) {
        if (dtoPage == null) {
            return null;
        }
        if (!dtoPage.hasContent()) {
            return PageResponse.empty();
        }

        Page<InventoryResponse> responsePage = dtoPage.map(this::toResponse);
        return PageResponse.from(responsePage);
    }
}
