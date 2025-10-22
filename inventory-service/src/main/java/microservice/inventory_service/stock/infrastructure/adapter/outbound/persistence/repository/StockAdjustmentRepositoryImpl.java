package microservice.inventory_service.stock.infrastructure.adapter.outbound.persistence.repository;

import microservice.inventory_service.inventory.domain.entity.enums.AdjustmentReason;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.AdjustmentId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.UserId;
import microservice.inventory_service.stock.domain.StockAdjustment;
import microservice.inventory_service.stock.domain.port.output.StockAdjustmentRepository;
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
