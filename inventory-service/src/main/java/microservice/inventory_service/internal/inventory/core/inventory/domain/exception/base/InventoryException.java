package microservice.inventory_service.internal.inventory.core.inventory.domain.exception.base;

import java.util.Map;

public class InventoryException extends RuntimeException {
    private final String errorCode;

    protected InventoryException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    protected InventoryException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    protected InventoryException(String message) {
        super(message);
        this.errorCode = "INVENTORY_ERROR";
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
