package microservice.inventory_service.inventory.domain.entity.valueobject;

import lombok.Builder;
import microservice.inventory_service.inventory.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.MedicineId;
import microservice.inventory_service.inventory.domain.exception.InventorMissingFieldException;
import microservice.inventory_service.inventory.domain.exception.InventoryException;

import java.util.Optional;

@Builder
public record CreateInventoryParams(
        MedicineId medicineId,
        String warehouseLocation,
        Integer reorderLevel,
        Integer reorderQuantity,
        Integer maximumStockLevel,
        Optional<InventoryBatch> initialBatch
) {

    public void validate() {
        if (medicineId == null) {
            throw new InventorMissingFieldException("Medicine ID cannot be null");
        }
        if (warehouseLocation == null || warehouseLocation.isBlank()) {
            throw new InventorMissingFieldException("Warehouse location cannot be null or blank to create an inventory");
        }
        if (reorderLevel == null || reorderLevel < 0) {
            throw new InventorMissingFieldException("Reorder level must be a non-negative integer to create an inventory");
        }
        if (reorderQuantity == null || reorderQuantity <= 0) {
            throw new InventorMissingFieldException("Reorder quantity must be a positive integer to create an inventory");
        }
        if (maximumStockLevel == null || maximumStockLevel <= 0) {
            throw new InventorMissingFieldException("Maximum stock level must be a positive integer to create an inventory");
        }

        if (reorderLevel >= maximumStockLevel) {
            throw new InventoryException("Reorder level must be less than maximum stock level");
        }
    }
}
