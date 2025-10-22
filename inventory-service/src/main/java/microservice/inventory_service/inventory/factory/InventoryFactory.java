package microservice.inventory_service.inventory.factory;


import microservice.inventory_service.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.domain.entity.enums.InventoryStatus;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.MedicineId;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class InventoryFactory {

    public static Inventory create(MedicineId medicineId, Integer initialQuantity, Integer reorderLevel, Integer reorderQuantity, Integer maximumStockLevel, String warehouseLocation) {
        validateParameters(initialQuantity, reorderLevel, reorderQuantity, maximumStockLevel);

        return Inventory.reconstructor().id(InventoryId.generate()).medicineId(medicineId).totalQuantity(initialQuantity).availableQuantity(initialQuantity).reservedQuantity(0).reorderLevel(reorderLevel).reorderQuantity(reorderQuantity).maximumStockLevel(maximumStockLevel).warehouseLocation(warehouseLocation).status(determineInitialStatus(initialQuantity, reorderLevel)).lastRestockedDate(LocalDateTime.now()).batches(new ArrayList<>()).movements(new ArrayList<>()).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).reconstruct();
    }

    private static void validateParameters(Integer initialQuantity, Integer reorderLevel, Integer reorderQuantity, Integer maximumStockLevel) {
        if (initialQuantity < 0) {
            throw new IllegalArgumentException("Initial quantity cannot be negative");
        }
        if (reorderLevel < 0) {
            throw new IllegalArgumentException("Reorder level cannot be negative");
        }
        if (reorderQuantity <= 0) {
            throw new IllegalArgumentException("Reorder quantity must be positive");
        }
        if (maximumStockLevel <= reorderLevel) {
            throw new IllegalArgumentException("Maximum stock level must be greater than reorder level");
        }
    }

    private static InventoryStatus determineInitialStatus(Integer quantity, Integer reorderLevel) {
        if (quantity <= 0) {
            return InventoryStatus.OUT_OF_STOCK;
        } else if (quantity <= reorderLevel) {
            return InventoryStatus.LOW_STOCK;
        } else {
            return InventoryStatus.ACTIVE;
        }
    }
}
