package io.github.alexisTrejo11.drugstore.products.core.domain.exception;

public class InvalidPriceException extends ProductValidationException {
    public InvalidPriceException(String message) {
        super(message);
    }
}

