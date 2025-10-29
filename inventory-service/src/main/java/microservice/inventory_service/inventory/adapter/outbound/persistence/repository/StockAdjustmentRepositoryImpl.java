package microservice.inventory_service.inventory.adapter.outbound.persistence.repository;

import microservice.inventory_service.inventory.core.inventory.domain.entity.enums.AdjustmentReason;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.AdjustmentId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.inventory.core.stock.domain.valueobject.StockAdjustment;
import microservice.inventory_service.inventory.core.stock.port.output.StockAdjustmentRepository;
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
