package io.github.alexisTrejo11.drugstore.products.core.domain.exception;

public class PrescriptionRequiredException extends ProductBaseException {
    public PrescriptionRequiredException(String message) {
        super(message, "PRESCRIPTION_REQUIRED");
    }
}
