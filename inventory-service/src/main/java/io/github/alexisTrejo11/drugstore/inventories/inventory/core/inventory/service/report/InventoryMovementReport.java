package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.InventoryMovement;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;

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