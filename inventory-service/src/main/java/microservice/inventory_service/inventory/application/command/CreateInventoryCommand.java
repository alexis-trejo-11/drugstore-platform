package microservice.inventory_service.inventory.application.command;

import lombok.Builder;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.MedicineId;

@Builder
public record CreateInventoryCommand(
        MedicineId medicineId,
        Integer initialQuantity,
        Integer reorderLevel,
        Integer reorderQuantity,
        Integer maximumStockLevel,
        String warehouseLocation
) {
}
