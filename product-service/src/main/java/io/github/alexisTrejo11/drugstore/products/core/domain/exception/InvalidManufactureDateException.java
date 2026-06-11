package io.github.alexisTrejo11.drugstore.products.core.domain.exception;

public class InvalidManufactureDateException extends ProductBaseException {
    public InvalidManufactureDateException(String message) {
        super(message, "INVALID_MANUFACTURE_DATE");
    }
}
