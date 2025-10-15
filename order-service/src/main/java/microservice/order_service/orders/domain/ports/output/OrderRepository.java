package microservice.order_service.orders.domain.ports.output;

import microservice.order_service.orders.application.queries.request.SearchOrdersQuery;
import microservice.order_service.orders.domain.models.Order;
import microservice.order_service.orders.domain.models.enums.OrderStatus;
import microservice.order_service.external.address.domain.model.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface OrderRepository {
    Page<Order> findBySpecification(SearchOrdersQuery query);

    Optional<Order> findByID(OrderID orderID);
    Optional<Order> findByUserIDAndOrderID(UserID customerID, OrderID orderID);
    Optional<Order> findByIDAndUserID(OrderID orderID, UserID customerID);

    boolean existsAnyByAddressIDAndOngoingStatus(AddressID addressID);


    Long countByUserID(UserID customerID);
    Long countByUserIDAndStatus(UserID customerID, OrderStatus status);

    Order save(Order order);
    void softDelete(Order order);
    void hardDelete(Order order);
}
