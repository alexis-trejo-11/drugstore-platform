package microservice.store_service.app.domain.exception;


public class StoreValidationException extends StoreDomainException {
	public StoreValidationException(String code) {
		super("Store with exactCode '" + code + "' already exists.");
	}
}
