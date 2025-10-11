package microservice.order_service.orders.domain.models.exceptions;

public class InvalidOrderItemsException extends OrderDomainException {

    public InvalidOrderItemsException(String message) {
        super(message, "invalid_order_items");
    }
}
