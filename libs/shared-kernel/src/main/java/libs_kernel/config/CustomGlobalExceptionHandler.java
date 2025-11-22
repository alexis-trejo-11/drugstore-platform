package libs_kernel.config;


import libs_kernel.exceptions.*;
import libs_kernel.response.Error;
import libs_kernel.response.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomGlobalExceptionHandler {
    /* Rate Limit Exception */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ResponseWrapper<?>> handleRateLimitExceededException(RateLimitExceededException ex) {
        Error error = new Error();
        error.setErrorCode("RATE_LIMIT_EXCEEDED");
        error.setErrorMessage(ex.getMessage());

        var response = ResponseWrapper.error("Too Many Requests", error);
        return new ResponseEntity<>(response, HttpStatus.TOO_MANY_REQUESTS);
    }

    /* Validation Data Exceptions */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldsErrorMap = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existingMessage, newMessage) -> existingMessage + ", " + newMessage));

        Error error = new Error();
        error.setErrorCode("INVALID_DATA_FORMAT");
        error.setErrorMessage("Request contains invalid data format");
        error.setDetails(fieldsErrorMap);

        var response = ResponseWrapper.badRequest(error);

        return ResponseEntity.unprocessableEntity().body(response);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ResponseWrapper<?>> handleDomainException(DomainException ex) {
        Error error = new Error();
        error.setErrorCode(ex.getErrorCode());
        error.setErrorMessage(ex.getMessage());
        error.setErrorType(ex.getClass().getSimpleName());
        error.setDetails(Map.of("message", ex.getMessage()));

        var response = ResponseWrapper.error("Domain Exception Occurred", error);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ResponseWrapper<?>> handleConflictException(ConflictException ex) {
        Error error = new Error();
        error.setErrorCode(ex.getErrorCode());
        error.setErrorMessage(ex.getMessage());
        error.setErrorType(ex.getClass().getSimpleName());

        var response = ResponseWrapper.error("Conflict Exception Occurred", error);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseWrapper<?>> handleNotFoundException(NotFoundException ex) {
        Error error = new Error();
        error.setErrorCode(ex.getErrorCode());
        error.setErrorMessage(ex.getMessage());
        error.setErrorType(ex.getClass().getSimpleName());


        var response = ResponseWrapper.error("Not Found Exception Occurred", error);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ResponseWrapper<?>> handleUnauthorizedException(UnauthorizedException ex) {
        Error error = new Error();
        error.setErrorCode(ex.getErrorCode());
        error.setErrorMessage(ex.getMessage());
        error.setErrorType(ex.getClass().getSimpleName());
        error.setDetails(Map.of("message", ex.getMessage()));

        var response = ResponseWrapper.error("Unauthorized Exception Occurred", error);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseWrapper<?>> handleForbiddenException(ForbiddenException ex) {
        Error error = new Error();
        error.setErrorCode(ex.getErrorCode());
        error.setErrorMessage(ex.getMessage());
        error.setErrorType(ex.getClass().getSimpleName());
        error.setDetails(Map.of("message", ex.getMessage()));

        var response = ResponseWrapper.error("Forbidden Exception Occurred", error);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /* Generic Exception */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<?>> handleException(Exception ex) {
        Error error = new Error();
        error.setErrorCode("INTERNAL_SERVER_ERROR");
        error.setErrorMessage("An unexpected error occurred");
        error.setErrorType(ex.getClass().getSimpleName());
        error.setDetails(Map.of("message", ex.getMessage()));

        var response = ResponseWrapper.error("Unhandler Exception Ocurred", error);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
