package microservice.order_service.application.queries.request;

import microservice.order_service.domain.models.valueobjects.OrderID;

public record GetOrderByIdQuery(OrderID orderID) {
    public GetOrderByIdQuery {
        if (orderID == null) {
            throw new IllegalArgumentException("orderID cannot be null or empty");
        }
    }

    public static GetOrderByIdQuery of(String orderID) {
        return new GetOrderByIdQuery(OrderID.of(orderID));
    }

    public OrderID getOrderID() {
        return orderID;
    }
}

