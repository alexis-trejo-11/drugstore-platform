package microservice.inventory_service.inventory.core.inventory.application.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.inventory_service.inventory.core.inventory.domain.entity.enums.InventoryStatus;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.ProductId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStockReport {
    private InventoryId inventoryId;
    private ProductId productId;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Integer reorderLevel;
    private InventoryStatus status;
    private Integer numberOfBatches;
    private Integer activeBatches;
    private Integer activeReservations;
    private Boolean needsReorder;
    private Integer recommendedOrderQuantity;
}