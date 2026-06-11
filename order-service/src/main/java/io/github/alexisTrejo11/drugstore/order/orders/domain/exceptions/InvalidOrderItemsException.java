package io.github.alexisTrejo11.drugstore.order.orders.domain.exceptions;

public class InvalidOrderItemsException extends OrderDomainException {

    public InvalidOrderItemsException(String message) {
        super(message, "invalid_order_items");
    }
}
