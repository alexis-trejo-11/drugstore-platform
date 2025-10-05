package microservice.order_service.application.exceptions;


import libs_kernel.exceptions.NotFoundException;
import microservice.order_service.domain.models.valueobjects.OrderID;

public class OrderNotFoundIDException extends NotFoundException {
    public OrderNotFoundIDException(OrderID orderId) {
        super("Order", orderId.value());
    }
}
