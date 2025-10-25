package microservice.inventory_service.external.order.domain.port.output;

import microservice.inventory_service.external.order.domain.entity.valueobject.OrderId;
import microservice.inventory_service.external.order.domain.entity.Order;
import microservice.inventory_service.external.order.domain.entity.valueobject.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order Order);

    Optional<Order> findById(OrderId id);

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findBySupplierId(String supplierId, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Page<Order> findByExpectedDeliveryDateBefore(LocalDateTime date, Pageable pageable);

    void delete(OrderId id);

    boolean existsById(OrderId id);
}
