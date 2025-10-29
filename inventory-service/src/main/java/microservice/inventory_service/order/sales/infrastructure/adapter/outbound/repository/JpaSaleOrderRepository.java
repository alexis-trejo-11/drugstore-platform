package microservice.inventory_service.order.sales.infrastructure.adapter.outbound.repository;

import microservice.inventory_service.order.sales.infrastructure.adapter.outbound.models.SaleOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSaleOrderRepository extends JpaRepository<SaleOrderEntity, String> {
}
