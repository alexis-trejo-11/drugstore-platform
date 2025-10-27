package microservice.inventory_service.internal.inventory.core.inventory.application.cqrs.handler.query;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.internal.inventory.core.inventory.port.InventoryRepository;
import microservice.inventory_service.internal.inventory.core.stock.application.query.GetLowStockInventoriesQuery;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetLowStockInventoriesQueryHandler {
    private final InventoryRepository inventoryRepository;
    
    @Transactional(readOnly = true)
    public Page<Inventory> handle(GetLowStockInventoriesQuery query) {
        return inventoryRepository.findLowStock(query.pagination());
    }
}