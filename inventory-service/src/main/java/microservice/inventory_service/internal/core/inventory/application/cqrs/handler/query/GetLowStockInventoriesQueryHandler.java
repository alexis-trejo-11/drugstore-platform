package microservice.inventory_service.internal.core.inventory.application.cqrs.handler.query;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.internal.core.inventory.port.InventoryOutputPort;
import microservice.inventory_service.internal.core.stock.application.query.GetLowStockInventoriesQuery;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetLowStockInventoriesQueryHandler {
    private final InventoryOutputPort inventoryRepository;
    
    @Transactional(readOnly = true)
    public Page<Inventory> handle(GetLowStockInventoriesQuery query) {
        return inventoryRepository.findLowStock(query.pagination());
    }
}