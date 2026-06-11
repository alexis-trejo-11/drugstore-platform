package io.github.alexisTrejo11.drugstore.order.orders.domain.exceptions;

public class InvalidOrderDataException extends OrderDomainException {
    public InvalidOrderDataException(String message) {
        super(message, "invalid_order_data");
    }
}
