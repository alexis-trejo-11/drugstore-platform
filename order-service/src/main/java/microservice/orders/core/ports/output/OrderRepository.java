package microservice.orders.core.ports.output;

import microservice.orders.core.models.Order;
import microservice.orders.core.models.enums.OrderStatus;
import microservice.orders.core.models.valueobjects.CustomerId;
import microservice.orders.core.models.valueobjects.OrderId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(OrderId orderId);
    List<Order> findByCustomerId(CustomerId customerId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByCustomerIdAndStatus(CustomerId customerId, OrderStatus status);
    List<Order> findOrdersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Order> findAll();
    void delete(OrderId orderId);
    boolean existsById(OrderId orderId);
    long countByStatus(OrderStatus status);
    List<Order> findActiveOrdersByCustomerId(CustomerId customerId);
}
