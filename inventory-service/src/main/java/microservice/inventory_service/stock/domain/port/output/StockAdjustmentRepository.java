package microservice.inventory_service.stock.domain.port.output;

import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory_service.stock.domain.StockAdjustment;
import microservice.inventory_service.inventory.domain.entity.enums.AdjustmentReason;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.AdjustmentId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.UserId;
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