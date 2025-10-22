package microservice.inventory_service.stock.infrastructure.adapter.outbound.persistence.repository;

import microservice.inventory_service.stock.infrastructure.adapter.outbound.persistence.model.StockAdjustmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAdjustmentRepository extends JpaRepository<StockAdjustmentEntity, String> {
}
