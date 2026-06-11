package io.github.alexisTrejo11.drugstore.order.orders.domain.exceptions;

public class MissingDeliveryAddressException extends OrderDomainException {
    public MissingDeliveryAddressException() {
        super("Delivery Address is required for this order request", "missing_delivery_address");
    }
}
