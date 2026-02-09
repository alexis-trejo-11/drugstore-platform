package microservice.product_service.app.core.domain.exception;

public class PrescriptionRequiredException extends ProductValidationException {
    public PrescriptionRequiredException(String message) {
        super(message);
    }
}
