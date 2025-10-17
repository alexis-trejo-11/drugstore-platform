package microservice.store_service.domain.exception;

public class StoreNotFoundException extends RuntimeException {
    public StoreNotFoundException(String field, String value) {
        super("Store with " + field + " '" + value + "' not found.");
    }
}
