package microservice.order_service.orders.application.queries.request;

import microservice.order_service.orders.domain.models.valueobjects.OrderID;

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

