package microservice.stock.domain.port.output;

import microservice.stock.domain.StockAdjustment;
import microservice.inventory.domain.entity.enums.AdjustmentReason;
import microservice.inventory.domain.entity.valueobject.id.AdjustmentID;
import microservice.inventory.domain.entity.valueobject.id.InventoryID;
import microservice.inventory.domain.entity.valueobject.id.UserID;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;


public interface StockAdjustmentRepository {
    StockAdjustment save(StockAdjustment adjustment);

    Optional<StockAdjustment> findById(AdjustmentID id);

    Page<StockAdjustment> findByInventoryId(InventoryID inventoryID);

    Page<StockAdjustment> findByReason(AdjustmentReason reason);

    Page<StockAdjustment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Page<StockAdjustment> findByPerformedBy(UserID userID);
}