package io.github.alexisTrejo11.drugstore.products.core.domain.exception;

public class InvalidPriceException extends ProductBaseException {
    public InvalidPriceException(String message) {
        super(message, "INVALID_PRICE");
    }
}
