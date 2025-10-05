package microservice.order_service.domain.ports.output;

import microservice.order_service.domain.models.Order;
import microservice.order_service.domain.models.enums.OrderStatus;
import microservice.order_service.domain.models.valueobjects.CustomerID;
import microservice.order_service.domain.models.valueobjects.OrderID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(OrderID orderId);
    Optional<Order> findByCustomerIdAndOrderId(CustomerID customerId, OrderID orderId);
    Page<Order> findByCustomerID(CustomerID customerId, Pageable pageable);
    Page<Order> findByCustomerIdAndOrderStatus(CustomerID customerId, OrderStatus status, Pageable pageable);
    Page<Order> findByCustomerIdAndRangeDate(CustomerID customerId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Optional<Order> findByCustomerIdAndId(CustomerID customerId, OrderID orderId);
    List<Order> findRecentOrdersByCustomer(CustomerID customerId, int limit);

    Long countByCustomerId(CustomerID customerId);
    Long countByCustomerIdAndStatus(CustomerID customerId, OrderStatus status);
    Order save(Order order);
    void softDelete(Order order);
    void hardDelete(Order order);
}
