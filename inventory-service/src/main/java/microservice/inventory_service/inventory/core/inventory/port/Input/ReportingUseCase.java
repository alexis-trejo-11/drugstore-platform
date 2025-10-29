package microservice.inventory_service.inventory.core.inventory.port.Input;

import microservice.inventory_service.inventory.core.inventory.application.report.BatchExpirationReport;
import microservice.inventory_service.inventory.core.inventory.application.report.InventoryMovementReport;
import microservice.inventory_service.inventory.core.inventory.application.report.InventoryStockReport;
import microservice.inventory_service.inventory.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportingUseCase {
    List<Inventory> getInventoriesNeedingReorder();
    InventoryStockReport generateStockReport(InventoryId inventoryId);
    InventoryMovementReport generateMovementReport(InventoryId inventoryId, LocalDateTime startDate, LocalDateTime endDate);
    BatchExpirationReport generateExpirationReport(Integer daysThreshold);
}