package microservice.order_service.orders.domain.models.exceptions;

import java.util.Map;

/**
 * Base class for all domain-specific exceptions in the Order context.
 * This class extends RuntimeException and includes an error code for better error handling.
 */
public abstract class OrderDomainException extends RuntimeException {
    private final String errorCode;

    protected OrderDomainException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    protected OrderDomainException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Map<String, Object> getLoggingContext() {
        return Map.of(
                "errorCode", errorCode,
                "exceptionType", this.getClass().getSimpleName(),
                "message", getMessage()
        );
    }
}
