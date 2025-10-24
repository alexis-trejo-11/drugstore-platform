package microservice.inventory_service.internal.core.inventory.application.cqrs.handler.query;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.core.inventory.application.cqrs.query.GetInventoryByMedicineQuery;
import microservice.inventory_service.internal.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.internal.core.inventory.domain.exception.InventoryNotFoundException;
import microservice.inventory_service.internal.core.inventory.port.InventoryRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetInventoryByMedicineQueryHandler {
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public Inventory handle(GetInventoryByMedicineQuery query) {
        return inventoryRepository.findByMedicineId(query.medicineId())
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for medicine"));
    }
}
