package microservice.order_service.domain.ports.output;

import microservice.order_service.domain.models.Order;
import microservice.order_service.domain.models.enums.OrderStatus;
import microservice.order_service.domain.models.valueobjects.CustomerId;
import microservice.order_service.domain.models.valueobjects.OrderId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Page<Order> findByCustomerID(CustomerId customerId, Pageable pageable);

    Page<Order> findByCustomerIdAndOrderStatus(CustomerId customerId, OrderStatus status, Pageable pageable);

    Page<Order> findByCustomerIdAndRangeDate(CustomerId customerId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Optional<Order> findByCustomerIdAndId(CustomerId customerId, OrderId orderId);

    List<Order> findRecentOrdersByCustomer(CustomerId customerId, int limit);

    Long countByCustomerId(CustomerId customerId);

    Long countByCustomerIdAndStatus(CustomerId customerId, OrderStatus status);
    Order save(Order order);

    Optional<Order> findByCustomerIdAndOrderId(CustomerId customerId, OrderId orderId);
}
