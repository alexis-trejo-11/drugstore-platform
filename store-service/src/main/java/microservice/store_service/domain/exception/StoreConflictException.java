package microservice.store_service.domain.exception;

public class StoreConflictException extends StoreDomainException {
    public StoreConflictException(String message) {
        super(message);
    }
}
