package io.github.alexisTrejo11.drugstore.order.orders.application.query.request;

import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.OrderID;

public record GetOrderDetailByIDQuery(OrderID orderID) {
    public GetOrderDetailByIDQuery(OrderID orderID) {
        if (orderID == null) {
            throw new IllegalArgumentException("orderID cannot be null or empty");
        }
        this.orderID = orderID;
    }

    public static GetOrderDetailByIDQuery of(String orderID) {
        return new GetOrderDetailByIDQuery(OrderID.of(orderID));
    }

}
