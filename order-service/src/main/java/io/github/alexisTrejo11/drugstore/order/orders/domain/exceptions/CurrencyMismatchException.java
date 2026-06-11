package io.github.alexisTrejo11.drugstore.order.orders.domain.exceptions;

public class CurrencyMismatchException extends RuntimeException {
    public CurrencyMismatchException(String message) {
        super(message);
    }
}
