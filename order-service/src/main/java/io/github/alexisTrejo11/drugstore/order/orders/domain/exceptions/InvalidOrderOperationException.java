package io.github.alexisTrejo11.drugstore.order.orders.domain.exceptions;

public class InvalidOrderOperationException extends RuntimeException {
    public InvalidOrderOperationException(String message) {
        super(message);
    }
}
