package microservice.inventory_service.internal.core.inventory.domain.entity.valueobject;

import lombok.Builder;
import microservice.inventory_service.internal.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.internal.core.inventory.domain.exception.InventorMissingFieldException;
import microservice.inventory_service.internal.core.inventory.domain.exception.InventoryValidationException;

import java.util.Optional;

@Builder
public record CreateInventoryParams(
        ProductId productId,
        String warehouseLocation,
        Integer reorderLevel,
        Integer reorderQuantity,
        Integer maximumStockLevel,
        Optional<InventoryBatch> initialBatch
) {

    public void validate() {
        if (productId == null) {
            throw new InventorMissingFieldException("Product Id cannot be null");
        }
        if (warehouseLocation == null || warehouseLocation.isBlank()) {
            throw new InventorMissingFieldException("Warehouse location cannot be null or blank to create an inventory");
        }
        if (reorderLevel == null || reorderLevel < 0) {
            throw new InventorMissingFieldException("Reorder level must be StockMovementUseCaseImpl non-negative integer to create an inventory");
        }
        if (reorderQuantity == null || reorderQuantity <= 0) {
            throw new InventorMissingFieldException("Reorder quantity must be StockMovementUseCaseImpl positive integer to create an inventory");
        }
        if (maximumStockLevel == null || maximumStockLevel <= 0) {
            throw new InventorMissingFieldException("Maximum stock level must be StockMovementUseCaseImpl positive integer to create an inventory");
        }

        if (reorderLevel >= maximumStockLevel) {
            throw new InventoryValidationException("Reorder level must be less than maximum stock level");
        }
    }
}
