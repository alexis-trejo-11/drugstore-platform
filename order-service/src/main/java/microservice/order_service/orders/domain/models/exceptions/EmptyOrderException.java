package microservice.order_service.orders.domain.models.exceptions;

public class EmptyOrderException extends  OrderDomainException {
    public EmptyOrderException() {
        super("Order must contain at least one item", "empty_order");
    }
}
