package microservice.product_service.app.domain.exception;

public class ProductValidationException extends RuntimeException {
  public ProductValidationException(String message) {
    super(message);
  }
}
