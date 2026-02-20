package io.github.alexisTrejo11.drugstore.products.core.domain.exception;

public class InvalidExpirationDateException extends ProductValidationException {
    public InvalidExpirationDateException(String message) {
        super(message);
    }
}
