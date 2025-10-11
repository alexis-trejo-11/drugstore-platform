package microservice.order_service.orders.domain.models.exceptions;

public class DeliveryMethodMismatchException extends OrderDomainException {
    public DeliveryMethodMismatchException(String message) {
        super(message, "delivery_method_mismatch");
    }
}
