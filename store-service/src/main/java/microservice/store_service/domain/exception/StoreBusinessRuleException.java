package microservice.store_service.domain.exception;

public class StoreBusinessRuleException extends RuntimeException {
    public StoreBusinessRuleException(String message) {
        super(message);
    }
}
