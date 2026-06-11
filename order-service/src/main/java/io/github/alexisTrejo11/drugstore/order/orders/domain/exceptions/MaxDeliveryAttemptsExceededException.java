package io.github.alexisTrejo11.drugstore.order.orders.domain.exceptions;

public class MaxDeliveryAttemptsExceededException extends OrderDomainException{
    public MaxDeliveryAttemptsExceededException(String message) {
        super(message, "order_max_delivery_attempts_exceeded");
    }
}
