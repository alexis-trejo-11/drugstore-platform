package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.handler.query;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.Inventory;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.enums.InventoryStatus;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.port.InventoryRepository;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.query.GetLowStockInventoriesQuery;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetLowStockInventoriesQueryHandler {
    private final InventoryRepository inventoryRepository;
    
    @Transactional(readOnly = true)
    public Page<Inventory> handle(GetLowStockInventoriesQuery query) {
        var inventories = inventoryRepository.findByStatus(InventoryStatus.LOW_STOCK, query.pagination());
        return inventories;
    }
}