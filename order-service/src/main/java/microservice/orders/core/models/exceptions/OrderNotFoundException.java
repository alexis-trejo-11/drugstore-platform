package microservice.orders.core.models.exceptions;

import microservice.orders.core.models.valueobjects.OrderId;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(OrderId orderId) {
        super("Order not found with ID: " + orderId);
    }
    
    public OrderNotFoundException(String message) {
        super(message);
    }
}
