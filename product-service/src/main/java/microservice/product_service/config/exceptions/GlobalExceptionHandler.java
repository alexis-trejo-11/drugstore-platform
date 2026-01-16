package microservice.product_service.app.infrastructure.in.web.exceptions;

import libs_kernel.response.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import microservice.product_service.app.domain.exception.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler for the Product Microservice.
 * This class intercepts various exceptions thrown by controllers and services
 * and translates them into consistent, user-friendly error responses
 * using the ResponseWrapper format.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * Handles validation exceptions thrown by @Valid annotations on DTOs.
     * Extracts all validation errors and returns them in a structured map.
     *
     * @param ex The MethodArgumentNotValidException instance.
     * @return A ResponseEntity containing a ResponseWrapper with validation errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            String errorCode = "DATA_VALIDATION_FAIL";
            errors.put(fieldName, errorMessage);
            errors.put("error_code", errorCode);
        });
        log.error("Validation error: {}", errors, ex);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.error(errors));
    }


    /**
     * Handles exceptions when the request body is malformed or unreadable (e.g., invalid JSON).
     *
     * @param ex The HttpMessageNotReadableException instance.
     * @return A ResponseEntity containing a ResponseWrapper with an error message.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseWrapper<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();
        String errorMessage = "Malformed JSON request body or invalid data type. Please check your request syntax.";
        String errorType = "HTTP Body";
        String errorCode= "INVALID_REQUEST_BODY";
        log.error("HTTP Message Not Readable: {}", ex.getMessage(), ex);

        errors.put("error_message", errorMessage);
        errors.put("error_type", errorType);
        errors.put("error_code", errorCode);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.error(errors));
    }


    /**
     * Handles exceptions when a required request parameter is missing.
     *
     * @param ex The MissingServletRequestParameterException instance.
     * @return A ResponseEntity containing a ResponseWrapper with an error message.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseWrapper<String>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        Map<String, String> errors = new HashMap<>();
        String errorMessage = String.format("Required request parameter '%s' is not present.", ex.getParameterName());
        String errorType = "HTTP URL";
        String errorCode = "MISSING_REQUEST_PARAM";
        log.error("HTTP Message Not Readable: {}", ex.getMessage(), ex);

        errors.put("error_message", errorMessage);
        errors.put("error_type", errorType);
        errors.put("error_code", errorCode);

        log.error("Missing request parameter: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.error(errors));
    }


    /**
     * Handles exceptions when the requested HTTP method is not supported for a given endpoint.
     *
     * @param ex The HttpRequestMethodNotSupportedException instance.
     * @return A ResponseEntity containing a ResponseWrapper with an error message.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseWrapper<String>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
            Map<String, String> errors = new HashMap<>();
            String errorMessage = "Method Not Allowed";
            String errorType = "HTTP Method";
            String errorCode = "METHOD_NOT_ALLOWED";

            errors.put("error_message", errorMessage);
            errors.put("error_type", errorType);
            errors.put("error_code", errorCode);

        log.error("Method not supported: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ResponseWrapper.error(errors));
    }

    /**
     * Handles the custom ProductNotFoundException.
     * This will return an HTTP 404 Not Found status.
     *
     * @param ex The ProductNotFoundException instance.
     * @return A ResponseEntity containing a ResponseWrapper with the product not found message.
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ResponseWrapper<String>> handleProductNotFoundException(ProductNotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        String errorMessage = "Product Not Found";
        String errorType = "App Error";
        String errorCode = "NOT_FOUND";

        errors.put("error_message", errorMessage);
        errors.put("error_type", errorType);
        errors.put("error_code", errorCode);

        log.warn("Product not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseWrapper.error(errors));
    }


    /**
     * General exception handler for any other unhandled exceptions.
     * This acts as a fallback to catch unexpected errors and prevent them from leaking
     * sensitive information to the client.
     *
     * @param ex The Exception instance.
     * @return A ResponseEntity containing a ResponseWrapper with a generic error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<String>> handleAllUncaughtExceptions(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        Map<String, String> errors = new HashMap<>();
        errors.put("error_message",  "An unexpected error occurred. Please try again later.");
        errors.put("error_type", "Not Handled Exception");
        errors.put("error_code", "Server_Error");


        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseWrapper.error(errors));
    }

}