package microservice.order_service.config.exception;

import libs_kernel.config.CustomGlobalExceptionHandler;
import libs_kernel.response.ErrorDetails;
import libs_kernel.response.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import microservice.order_service.orders.domain.models.exceptions.OrderDomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends CustomGlobalExceptionHandler  {
    @ExceptionHandler(OrderDomainException.class)
    public ResponseEntity<ResponseWrapper<?>> handleOrderDomainException(OrderDomainException ex,
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

        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorCode(ex.getErrorCode());
        errorDetails.setErrorMessage(ex.getMessage());
        errorDetails.setErrorType(ex.getClass().getSimpleName());

        var response = ResponseWrapper.error("Domain Exception Occurred", errorDetails);
        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }


    private HttpStatus determineHttpStatus(OrderDomainException ex) {
        return switch (ex.getErrorCode()) {
            case "ORDER_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "DUPLICATE_PRODUCT_IN_ORDER",
                 "INVALID_STATE_TRANSITION",
                 "EMPTY_ORDER",
                 "CURRENCY_MISMATCH" -> HttpStatus.UNPROCESSABLE_ENTITY;
            case "ORDER_NOT_CANCELLABLE",
                 "ADDRESS_CHANGE_NOT_ALLOWED" -> HttpStatus.CONFLICT;
            default -> HttpStatus.BAD_REQUEST;
        };
    }


    private boolean shouldLogAsError(OrderDomainException ex) {
        return switch (ex.getErrorCode()) {
            case "MAX_DELIVERY_ATTEMPTS_EXCEEDED",
                 "CURRENCY_MISMATCH",
                 "INVALID_ORDER_DATA" -> true;
            default -> false; // Most are validation errors (WARN)
        };
    }
}
