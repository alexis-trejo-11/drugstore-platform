package microservice.inventory.application.handler.query;

import lombok.RequiredArgsConstructor;
import microservice.inventory.domain.entity.Inventory;
import microservice.inventory.domain.port.output.InventoryRepository;
import microservice.stock.application.query.GetLowStockInventoriesQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetLowStockInventoriesQueryHandler {
    private final InventoryRepository inventoryRepository;
    
    @Transactional(readOnly = true)
    public List<Inventory> handle(GetLowStockInventoriesQuery query) {
        List<Inventory> lowStockInventories = inventoryRepository.findLowStock();
        
        if (query.limit() != null && query.limit() > 0) {
            return lowStockInventories.stream()
                .limit(query.limit())
                .toList();
        }
        
        return lowStockInventories;
    }
}