package microservice.inventory_service.inventory.core.inventory.service.cqrs.handler.query;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.query.GetInventoryByProductQuery;
import microservice.inventory_service.inventory.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.core.inventory.domain.exception.InventoryNotFoundException;
import microservice.inventory_service.inventory.core.inventory.port.InventoryRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetInventoryByProductQueryHandler {
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public Inventory handle(GetInventoryByProductQuery query) {
        return inventoryRepository.findByProductId(query.productId())
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product: " + query.productId()));
    }
}
