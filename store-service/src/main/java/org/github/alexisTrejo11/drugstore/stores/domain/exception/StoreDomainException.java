package org.github.alexisTrejo11.drugstore.stores.domain.exception;

import java.util.Map;

public class StoreDomainException extends RuntimeException {
    private final String errorCode;

    protected StoreDomainException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    protected StoreDomainException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    protected StoreDomainException(String message) {
        super(message);
        this.errorCode = "STORE_DOMAIN_ERROR";
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
