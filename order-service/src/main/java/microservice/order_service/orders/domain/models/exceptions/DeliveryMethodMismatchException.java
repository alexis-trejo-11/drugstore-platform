package microservice.order_service.orders.domain.models.exceptions;


import java.util.Map;

public class DeliveryMethodMismatchException extends OrderDomainException {
    public DeliveryMethodMismatchException(String message) {
        super(message, "delivery_method_mismatch");
    }
}
