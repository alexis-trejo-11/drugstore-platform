package microservice.inventory.application.service.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.inventory.domain.entity.InventoryMovement;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;

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