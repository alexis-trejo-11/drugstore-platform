package microservice.order_service.orders.domain.models.exceptions;

import libs_kernel.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class OrderDomainException extends DomainException {
    public OrderDomainException(String message,  String errorCode) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, errorCode);
    }
}
