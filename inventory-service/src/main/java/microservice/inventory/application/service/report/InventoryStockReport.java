package microservice.inventory.application.service.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.inventory.domain.entity.enums.InventoryStatus;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory.domain.entity.valueobject.id.MedicineId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStockReport {
    private InventoryId inventoryId;
    private MedicineId medicineId;
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