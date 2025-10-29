package microservice.inventory_service.config.exception;

import libs_kernel.config.CustomGlobalExceptionHandler;
import libs_kernel.response.ErrorDetails;
import libs_kernel.response.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import microservice.inventory_service.inventory.core.inventory.domain.exception.base.InventoryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends CustomGlobalExceptionHandler  {
    @ExceptionHandler(InventoryException.class)
    public ResponseEntity<ResponseWrapper<?>> handleOrderDomainException(InventoryException ex, WebRequest request) {
        HttpStatus status = determineHttpStatus(ex);
        if (shouldLogAsError(ex)) {
            log.error("PurchaseOrder domain exception occurred: errorCode={}, context={}",
                    ex.getErrorCode(),
                    ex.getLoggingContext(),
                    ex);
        } else {
            log.warn("PurchaseOrder domain validation failed: errorCode={}, context={}",
                    ex.getErrorCode(),
                    ex.getLoggingContext());
        }

        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorCode(ex.getErrorCode());
        errorDetails.setErrorMessage(ex.getMessage());
        errorDetails.setErrorType(ex.getClass().getSimpleName());

        var response = ResponseWrapper.error("Domain Exception Occurred", errorDetails);
        return new ResponseEntity<>(response, status);
    }


    private HttpStatus determineHttpStatus(InventoryException ex) {
        return switch (ex.getErrorCode()) {
            case "INVENTORY_NOT_FOUND", "ORDER_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "PRODUCT_CONFLICT_ERROR" -> HttpStatus.CONFLICT;
            default -> HttpStatus.UNPROCESSABLE_ENTITY;
        };
    }


    private boolean shouldLogAsError(InventoryException ex) {
        return switch (ex.getErrorCode()) {
            case "PRODUCT_CONFLICT_ERROR" -> true;
            default -> false; // Most are validation errors (WARN)
        };
    }
}
