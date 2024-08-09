package at.backend.drugstore.microservice.common_models.GlobalExceptions;

import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomGlobalExceptionHandler {

    /* Validation Data Exceptions */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        String errorMessage = String.join(", ", errors);

        ResponseWrapper<Void> responseWrapper = new ResponseWrapper<>(
                false,
                null,
                errorMessage,
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.badRequest().body(responseWrapper);
    }

    /* Entity Not Found Exception */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ResponseWrapper<Void> responseWrapper = new ResponseWrapper<>(
                false,
                null,
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
    }

    /* To Many Request Exception */
    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<String> handleRequestNotPermitted(RequestNotPermitted exception) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Rate limit exceeded. Please try again later.");
    }

    /* Generic Exception */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<String>> handleException(Exception ex) {
        ResponseWrapper<String> response = new ResponseWrapper<>(false, null, "An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
