package io.github.alexisTrejo11.drugstore.products.core.domain.exception;

public class PrescriptionRequiredException extends ProductValidationException {
    public PrescriptionRequiredException(String message) {
        super(message);
    }
}
