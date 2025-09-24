package microservice.order_service.domain.ports.output;

import microservice.order_service.domain.models.Order;
import microservice.order_service.domain.models.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Page<Order> findByCustomerIdOrderByCreatedAtDesc(String customerId, Pageable pageable);

    Page<Order> findByCustomerIdAndStatusOrderByCreatedAtDesc(String customerId, OrderStatus status, Pageable pageable);

    Page<Order> findByCustomerIdAndCreatedAtBetweenOrderByCreatedAtDesc(String customerId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Optional<Order> findByCustomerIdAndId(String customerId, String orderId);

    List<Order> findTop5ByCustomerIdOrderByCreatedAtDesc(String customerId, Pageable pageable);

    Long countByCustomerId(String customerId);

    Long countByCustomerIdAndStatus(String customerId, OrderStatus status);
    Order save(Order order);

    Optional<Order> findByCustomerIdAndOrderId(String customerId, String orderId);
}
