package io.github.alexisTrejo11.drugstore.order.orders.domain.exceptions;

public class InvalidOrderStateException extends OrderDomainException{
    public InvalidOrderStateException(String message) {
        super(message, "invalid_order_state");
    }
}
