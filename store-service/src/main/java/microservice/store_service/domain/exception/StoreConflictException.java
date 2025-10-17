package microservice.store_service.domain.exception;

public class StoreConflictException extends RuntimeException {
    public StoreConflictException(String message) {
        super(message);
    }
}
