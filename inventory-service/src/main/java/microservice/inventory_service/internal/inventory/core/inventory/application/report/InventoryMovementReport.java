package microservice.inventory_service.internal.inventory.core.inventory.application.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.inventory_service.internal.inventory.core.movement.domain.InventoryMovement;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.InventoryId;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovementReport {
    private InventoryId inventoryId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer totalMovements;
    private Integer totalReceipts;
    private Integer totalSales;
    private Integer totalAdjustments;
    private Integer totalDamage;
    private Integer totalExpiration;
    private List<InventoryMovement> movements;
}