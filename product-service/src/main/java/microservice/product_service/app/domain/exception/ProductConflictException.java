package microservice.product_service.app.domain.exception;

public class ProductConflictException extends ProductValidationException {
  public ProductConflictException(String message) {
    super(message);
  }
}
