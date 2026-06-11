package io.github.alexisTrejo11.drugstore.order.orders.application.query.request;

import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.OrderID;

import java.util.UUID;


public record GetOrderByIDAndUserIDQuery(
        UserID userID,
        OrderID orderID) {

    public static GetOrderByIDAndUserIDQuery of(UUID userID, UUID orderID) {
        return new GetOrderByIDAndUserIDQuery(UserID.of(userID), OrderID.of(orderID));
    }

    public static GetOrderByIDAndUserIDQuery of(String userID, String orderID) {
        return new GetOrderByIDAndUserIDQuery(UserID.of(userID), OrderID.of(orderID));
    }
}
