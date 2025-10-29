package microservice.inventory_service.order.sales.core.domain.port;

import microservice.inventory_service.order.sales.core.domain.entity.SaleOrder;
import microservice.inventory_service.order.sales.core.domain.entity.valueobject.SaleOrderId;
import microservice.inventory_service.shared.domain.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SaleOrderRepository {
    Optional<SaleOrder> findById(SaleOrderId saleOrderId);
    Page<SaleOrder> findByCustomerId(String userId, Pageable pageable);
    Page<SaleOrder> findByStatus(OrderStatus status, Pageable pageable);

    SaleOrder save(SaleOrder saleOrder);
    boolean existsById(SaleOrderId saleOrderId);
    void deleteById(SaleOrderId saleOrderId);

}
