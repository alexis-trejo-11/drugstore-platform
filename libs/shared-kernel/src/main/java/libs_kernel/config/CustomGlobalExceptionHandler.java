package libs_kernel.config;


import libs_kernel.exceptions.*;
import libs_kernel.response.ErrorDetails;
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
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorCode("RATE_LIMIT_EXCEEDED");
        errorDetails.setErrorMessage(ex.getMessage());

        var response = ResponseWrapper.error("Too Many Requests", errorDetails);
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

        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorCode("INVALID_DATA_FORMAT");
        errorDetails.setErrorMessage("Request contains invalid data format");
        errorDetails.setDetails(fieldsErrorMap);

        var response = ResponseWrapper.badRequest(errorDetails);

        return ResponseEntity.unprocessableEntity().body(response);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ResponseWrapper<?>> handleDomainException(DomainException ex) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorCode(ex.getErrorCode());
        errorDetails.setErrorMessage(ex.getMessage());
        errorDetails.setErrorType(ex.getClass().getSimpleName());
        errorDetails.setDetails(Map.of("message", ex.getMessage()));

        var response = ResponseWrapper.error("Domain Exception Occurred", errorDetails);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ResponseWrapper<?>> handleConflictException(ConflictException ex) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorCode(ex.getErrorCode());
        errorDetails.setErrorMessage(ex.getMessage());
        errorDetails.setErrorType(ex.getClass().getSimpleName());

        var response = ResponseWrapper.error("Conflict Exception Occurred", errorDetails);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseWrapper<?>> handleNotFoundException(NotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorCode(ex.getErrorCode());
        errorDetails.setErrorMessage(ex.getMessage());
        errorDetails.setErrorType(ex.getClass().getSimpleName());


        var response = ResponseWrapper.error("Not Found Exception Occurred", errorDetails);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ResponseWrapper<?>> handleUnauthorizedException(UnauthorizedException ex) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorCode(ex.getErrorCode());
        errorDetails.setErrorMessage(ex.getMessage());
        errorDetails.setErrorType(ex.getClass().getSimpleName());
        errorDetails.setDetails(Map.of("message", ex.getMessage()));

        var response = ResponseWrapper.error("Unauthorized Exception Occurred", errorDetails);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseWrapper<?>> handleForbiddenException(ForbiddenException ex) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorCode(ex.getErrorCode());
        errorDetails.setErrorMessage(ex.getMessage());
        errorDetails.setErrorType(ex.getClass().getSimpleName());
        errorDetails.setDetails(Map.of("message", ex.getMessage()));

        var response = ResponseWrapper.error("Forbidden Exception Occurred", errorDetails);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /* Generic Exception */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<?>> handleException(Exception ex) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorCode("INTERNAL_SERVER_ERROR");
        errorDetails.setErrorMessage("An unexpected error occurred");
        errorDetails.setErrorType(ex.getClass().getSimpleName());
        errorDetails.setDetails(Map.of("message", ex.getMessage()));

        var response = ResponseWrapper.error("Unhandler Exception Ocurred", errorDetails);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
