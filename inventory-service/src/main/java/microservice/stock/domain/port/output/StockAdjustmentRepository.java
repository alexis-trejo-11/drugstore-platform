package microservice.stock.domain.port.output;

import microservice.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.stock.domain.StockAdjustment;
import microservice.inventory.domain.entity.enums.AdjustmentReason;
import microservice.inventory.domain.entity.valueobject.id.AdjustmentId;
import microservice.inventory.domain.entity.valueobject.id.UserId;
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