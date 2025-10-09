package microservice.order_service.orders.domain.ports.output;

import microservice.order_service.orders.application.queries.request.SearchOrdersQuery;
import microservice.order_service.orders.domain.models.Order;
import microservice.order_service.orders.domain.models.enums.OrderStatus;
import microservice.order_service.orders.domain.models.valueobjects.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository {
    Page<Order> findBySpecification(SearchOrdersQuery query);

    Optional<Order> findByID(OrderID orderID);
    Optional<Order> findByUserIDAndOrderID(UserID customerID, OrderID orderID);
    Optional<Order> findByUserIDAndID(UserID customerID, OrderID orderID);

    Page<Order> findByUserID(UserID customerID, Pageable pageable);
    Page<Order> findByUserIDAndOrderStatus(UserID customerID, OrderStatus status, Pageable pageable);
    Page<Order> findByUserIDAndRangeDate(UserID customerID, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    boolean existsAnyByAddressIDAndOngoingStatus(AddressID addressID);


    Long countByUserID(UserID customerID);
    Long countByUserIDAndStatus(UserID customerID, OrderStatus status);

    Order save(Order order);
    void softDelete(Order order);
    void hardDelete(Order order);
}
