package microservice.inventory_service.inventory.core.stock.port.output;

import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.stock.domain.valueobject.StockAdjustment;
import microservice.inventory_service.inventory.core.inventory.domain.entity.enums.AdjustmentReason;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.AdjustmentId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;


public interface StockAdjustmentRepository {
    StockAdjustment save(StockAdjustment adjustment);

    Optional<StockAdjustment> findById(AdjustmentId id);

    Page<StockAdjustment> findByInventoryId(InventoryId inventoryID);

    Page<StockAdjustment> findByReason(AdjustmentReason reason);

    Page<StockAdjustment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Page<StockAdjustment> findByPerformedBy(UserId userID);
}