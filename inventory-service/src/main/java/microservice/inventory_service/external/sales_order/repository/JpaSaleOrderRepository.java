package microservice.inventory_service.external.sales_order.repository;

import microservice.inventory_service.external.sales_order.model.SaleOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSaleOrderRepository extends JpaRepository<SaleOrderEntity, String> {
}
