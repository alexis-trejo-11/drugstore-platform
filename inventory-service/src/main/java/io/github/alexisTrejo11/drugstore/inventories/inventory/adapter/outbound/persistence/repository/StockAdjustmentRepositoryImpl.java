package io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.outbound.persistence.repository;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.enums.AdjustmentReason;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.AdjustmentId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.UserId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.valueobject.StockAdjustment;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.port.output.StockAdjustmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class StockAdjustmentRepositoryImpl implements StockAdjustmentRepository {
    @Override
    public StockAdjustment save(StockAdjustment adjustment) {
        return null;
    }

    @Override
    public Optional<StockAdjustment> findById(AdjustmentId id) {
        return Optional.empty();
    }

    @Override
    public Page<StockAdjustment> findByInventoryId(InventoryId inventoryID) {
        return null;
    }

    @Override
    public Page<StockAdjustment> findByReason(AdjustmentReason reason) {
        return null;
    }

    @Override
    public Page<StockAdjustment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return null;
    }

    @Override
    public Page<StockAdjustment> findByPerformedBy(UserId userID) {
        return null;
    }
}
