package io.github.alexisTrejo11.drugstore.order.orders.application.query.request;

import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.OrderID;

public record GetOrderByIDQuery(OrderID orderID) {
    public GetOrderByIDQuery {
        if (orderID == null) {
            throw new IllegalArgumentException("orderID cannot be null or empty");
        }
    }

    public static GetOrderByIDQuery of(String orderID) {
        return new GetOrderByIDQuery(OrderID.of(orderID));
    }

    public OrderID getOrderID() {
        return orderID;
    }
}

