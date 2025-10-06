package microservice.order_service.orders.domain.models.exceptions;

import microservice.order_service.orders.domain.models.valueobjects.OrderID;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(OrderID orderId) {
        super("Order not found with ID: " + orderId);
    }
    
    public OrderNotFoundException(String message) {
        super(message);
    }
}
