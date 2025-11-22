package microservice.store_service.config;

import libs_kernel.config.CustomGlobalExceptionHandler;
import libs_kernel.response.Error;
import libs_kernel.response.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import microservice.store_service.domain.exception.StoreDomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends CustomGlobalExceptionHandler  {
    @ExceptionHandler(StoreDomainException.class)
    public ResponseEntity<ResponseWrapper<?>> handleOrderDomainException(StoreDomainException ex,
                                                                         WebRequest request) {
        HttpStatus status = determineHttpStatus(ex);
        if (shouldLogAsError(ex)) {
            log.error("Order domain exception occurred: errorCode={}, context={}",
                    ex.getErrorCode(),
                    ex.getLoggingContext(),
                    ex);
        } else {
            log.warn("Order domain validation failed: errorCode={}, context={}",
                    ex.getErrorCode(),
                    ex.getLoggingContext());
        }

        Error error = new Error();
        error.setErrorCode(ex.getErrorCode());
        error.setErrorMessage(ex.getMessage());
        error.setErrorType(ex.getClass().getSimpleName());

        var response = ResponseWrapper.error("Domain Exception Occurred", error);
        return new ResponseEntity<>(response, status);
    }


    private HttpStatus determineHttpStatus(StoreDomainException ex) {
        return switch (ex.getErrorCode()) {
            case "STORE_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "STORE_CONFLICT" -> HttpStatus.CONFLICT;
            default -> HttpStatus.UNPROCESSABLE_ENTITY;
        };
    }


    private boolean shouldLogAsError(StoreDomainException ex) {
        return switch (ex.getErrorCode()) {
            case "MAX_DELIVERY_ATTEMPTS_EXCEEDED",
                 "CURRENCY_MISMATCH",
                 "INVALID_ORDER_DATA" -> true;
            default -> false; // Most are validation errors (WARN)
        };
    }
}
