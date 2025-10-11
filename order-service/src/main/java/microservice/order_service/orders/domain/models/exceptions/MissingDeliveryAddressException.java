package microservice.order_service.orders.domain.models.exceptions;

import microservice.order_service.orders.domain.models.valueobjects.OrderID;

public class MissingDeliveryAddressException extends OrderDomainException {
    public MissingDeliveryAddressException() {
        super("Delivery Address is required for this order request", "missing_delivery_address");
    }
}
