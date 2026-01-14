package microservice.product_service.app.domain.exception;

public class ProductValueObjectException extends RuntimeException {
    private final String valueObject;

    public ProductValueObjectException(String message) {
        super(message);
        this.valueObject = null;
    }

    public ProductValueObjectException(String valueObject, String message) {
        super(message);
        this.valueObject = valueObject;
    }

    public ProductValueObjectException(String valueObject, String message, Throwable cause) {
        super(message, cause);
        this.valueObject = valueObject;
    }

    public String getValueObject() {
        return valueObject;
    }

    @Override
    public String toString() {
        if (valueObject == null) return super.toString();
        return String.format("%s: %s", valueObject, getMessage());
    }
}
