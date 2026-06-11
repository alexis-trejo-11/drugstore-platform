package io.github.alexisTrejo11.drugstore.order.orders.domain.exceptions;

public class InvalidOrderException extends OrderDomainException{
    public InvalidOrderException(String message) {
        super(message, "invalid_order");
    }
}
