package microservice.store_service.app.domain.exception;

public class StoreConflictException extends StoreDomainException {
    public StoreConflictException(String message) {
        super(message);
    }
}
