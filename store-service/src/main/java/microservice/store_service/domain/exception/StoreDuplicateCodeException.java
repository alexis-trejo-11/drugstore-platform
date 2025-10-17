package microservice.store_service.domain.exception;

public class StoreDuplicateCodeException extends RuntimeException {
    public StoreDuplicateCodeException(String code) {
        super("Store with code '" + code + "' already exists.");
    }
}
