package microservice.inventory_service.inventory.application.handler.query;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.application.query.GetInventoryByIdQuery;
import microservice.inventory_service.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.domain.exception.InventoryNotFoundException;
import microservice.inventory_service.inventory.domain.port.output.InventoryRepository;
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
