package libs_kernel.exceptions;

import org.springframework.http.HttpStatus;

public class ValidationException extends DomainException {
    public ValidationException(String message, String errorCode) {
        super(message, HttpStatus.BAD_REQUEST, errorCode);
    }
}
