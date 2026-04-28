package microservice.order_service.orders.application.queries.request;

import microservice.order_service.orders.domain.models.valueobjects.OrderID;

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
