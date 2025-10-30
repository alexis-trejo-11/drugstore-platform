package microservice.inventory_service.inventory.core.inventory.application.cqrs.handler.query;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.core.inventory.domain.entity.enums.InventoryStatus;
import microservice.inventory_service.inventory.core.inventory.port.InventoryRepository;
import microservice.inventory_service.inventory.core.stock.application.query.GetLowStockInventoriesQuery;
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