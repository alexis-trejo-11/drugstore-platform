package io.github.alexisTrejo11.drugstore.inventories.config;

import libs_kernel.config.CustomGlobalExceptionHandler;
import libs_kernel.response.Error;
import libs_kernel.response.ResponseWrapper;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.InventoryNotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(0)
public class GlobalExceptionHandler extends CustomGlobalExceptionHandler {

    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<ResponseWrapper<?>> handleInventoryNotFound(InventoryNotFoundException ex) {
        Error error = new Error();
        error.setErrorCode(ex.getErrorCode());
        error.setErrorMessage(ex.getMessage());
        error.setErrorType("InventoryNotFoundException");
        ResponseWrapper<?> body = ResponseWrapper.error(ex.getMessage(), error);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
