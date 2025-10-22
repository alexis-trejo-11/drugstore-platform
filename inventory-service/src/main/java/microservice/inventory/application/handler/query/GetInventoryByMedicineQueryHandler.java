package microservice.inventory.application.handler.query;

import lombok.RequiredArgsConstructor;
import microservice.inventory.application.query.GetInventoryByMedicineQuery;
import microservice.inventory.domain.entity.Inventory;
import microservice.inventory.domain.exception.InventoryNotFoundException;
import microservice.inventory.domain.port.output.InventoryRepository;
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
