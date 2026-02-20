package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.port.output;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.valueobject.StockAdjustment;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.enums.AdjustmentReason;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.AdjustmentId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.UserId;
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