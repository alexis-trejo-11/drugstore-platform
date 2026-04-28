package io.github.alexisTrejo11.drugstore.products.core.domain.exception;

public class InvalidManufactureDateException extends ProductValidationException {
    public InvalidManufactureDateException(String message) {
        super(message);
    }
}
