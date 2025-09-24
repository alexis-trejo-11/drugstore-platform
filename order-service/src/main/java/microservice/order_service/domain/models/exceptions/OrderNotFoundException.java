package microservice.order_service.domain.models.exceptions;

import microservice.order_service.domain.models.valueobjects.OrderId;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(OrderId orderId) {
        super("Order not found with ID: " + orderId);
    }
    
    public OrderNotFoundException(String message) {
        super(message);
    }
}
