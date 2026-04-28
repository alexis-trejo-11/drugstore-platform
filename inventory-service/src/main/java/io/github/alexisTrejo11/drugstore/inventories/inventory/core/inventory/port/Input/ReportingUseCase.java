package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.port.Input;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.report.BatchExpirationReport;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.report.InventoryMovementReport;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.report.InventoryStockReport;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.Inventory;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportingUseCase {
    List<Inventory> getInventoriesNeedingReorder();
    InventoryStockReport generateStockReport(InventoryId inventoryId);
    InventoryMovementReport generateMovementReport(InventoryId inventoryId, LocalDateTime startDate, LocalDateTime endDate);
    BatchExpirationReport generateExpirationReport(Integer daysThreshold);
}