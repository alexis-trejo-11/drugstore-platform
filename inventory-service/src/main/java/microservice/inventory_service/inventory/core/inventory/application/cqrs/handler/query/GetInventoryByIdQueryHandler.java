package microservice.inventory_service.inventory.core.inventory.application.cqrs.handler.query;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.inventory.application.cqrs.query.GetInventoryByIdQuery;
import microservice.inventory_service.inventory.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.core.inventory.domain.exception.InventoryNotFoundException;
import microservice.inventory_service.inventory.core.inventory.port.InventoryRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetInventoryByIdQueryHandler {
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public Inventory handle(GetInventoryByIdQuery query) {
        return inventoryRepository.findById(query.inventoryId())
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for id"));
    }
}
