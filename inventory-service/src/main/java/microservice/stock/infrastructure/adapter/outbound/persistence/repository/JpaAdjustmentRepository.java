package microservice.stock.infrastructure.adapter.outbound.persistence.repository;

import microservice.stock.infrastructure.adapter.outbound.persistence.model.StockAdjustmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAdjustmentRepository extends JpaRepository<StockAdjustmentEntity, String> {
}
