package microservice.order_service.application.queries.request;

import microservice.order_service.domain.models.valueobjects.CustomerID;
import microservice.order_service.domain.models.valueobjects.OrderID;

import java.util.UUID;


public record GetOrderByCustomerAndIdQuery(CustomerID customerId, OrderID orderId) {
    public GetOrderByCustomerAndIdQuery {
        if (customerId == null) {
            throw new IllegalArgumentException("customerId cannot be null or empty");
        }
        if (orderId == null) {
            throw new IllegalArgumentException("orderId cannot be null or empty");
        }
    }

    public static GetOrderByCustomerAndIdQuery of(UUID customerId, UUID orderId) {
        return new GetOrderByCustomerAndIdQuery(CustomerID.of(customerId), OrderID.of(orderId));
    }

    public static GetOrderByCustomerAndIdQuery of(String customerId, String orderId) {
        return new GetOrderByCustomerAndIdQuery(CustomerID.of(customerId), OrderID.of(orderId));
    }
}
