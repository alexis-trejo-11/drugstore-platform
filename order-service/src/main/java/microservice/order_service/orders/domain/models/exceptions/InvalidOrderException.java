package microservice.order_service.orders.domain.models.exceptions;

import libs_kernel.exceptions.DomainException;

public class InvalidOrderException extends OrderDomainException{
    public InvalidOrderException(String message) {
        super(message, "invalid_order");
    }
}
