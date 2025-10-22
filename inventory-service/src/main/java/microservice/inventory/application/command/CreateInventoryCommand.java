package microservice.inventory.application.command;

import lombok.Builder;
import microservice.inventory.domain.entity.valueobject.id.MedicineId;

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
