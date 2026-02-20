package io.github.alexisTrejo11.drugstore.products.core.domain.exception;

public class InvalidProductDataException extends RuntimeException {
    public InvalidProductDataException(String message) {
        super(message);
    }
}
