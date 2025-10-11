package microservice.order_service.orders.domain.models.exceptions;

public class MaxDeliveryAttemptsExceededException extends OrderDomainException{
    public MaxDeliveryAttemptsExceededException(String message) {
        super(message, "order_max_delivery_attempts_exceeded");
    }
}
