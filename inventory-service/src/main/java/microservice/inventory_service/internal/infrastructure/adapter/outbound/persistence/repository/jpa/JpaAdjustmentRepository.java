package microservice.inventory_service.internal.infrastructure.adapter.outbound.persistence.repository.jpa;

import microservice.inventory_service.internal.infrastructure.adapter.outbound.persistence.model.StockAdjustmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAdjustmentRepository extends JpaRepository<StockAdjustmentEntity, String> {
}
