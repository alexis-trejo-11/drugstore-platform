package io.github.alexisTrejo11.drugstore.order.orders.domain.repository;

import io.github.alexisTrejo11.drugstore.order.orders.application.query.request.SearchOrdersQuery;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.Order;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.enums.OrderStatus;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.AddressID;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.OrderID;

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
