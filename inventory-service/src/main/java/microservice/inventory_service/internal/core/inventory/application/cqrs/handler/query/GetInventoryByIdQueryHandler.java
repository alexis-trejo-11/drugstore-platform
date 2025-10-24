package microservice.inventory_service.internal.core.inventory.application.cqrs.handler.query;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.core.inventory.application.cqrs.query.GetInventoryByIdQuery;
import microservice.inventory_service.internal.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.internal.core.inventory.domain.exception.InventoryNotFoundException;
import microservice.inventory_service.internal.core.inventory.port.InventoryOutputPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetInventoryByIdQueryHandler {
    private final InventoryOutputPort inventoryRepository;

    @Transactional(readOnly = true)
    public Inventory handle(GetInventoryByIdQuery query) {
        return inventoryRepository.findById(query.inventoryId())
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for id"));
    }
}
