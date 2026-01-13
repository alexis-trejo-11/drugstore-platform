package microservice.store_service.app.domain.exception;

public class StoreDuplicateCodeException extends StoreDomainException {
    public StoreDuplicateCodeException(String code) {
        super("Store with exactCode '" + code + "' already exists.");
    }
}
