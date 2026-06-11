package io.github.alexisTrejo11.drugstore.order.orders.domain.exceptions;


public class DeliveryMethodMismatchException extends OrderDomainException {
    public DeliveryMethodMismatchException(String message) {
        super(message, "delivery_method_mismatch");
    }
}
