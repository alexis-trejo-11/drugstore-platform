package microservice.auth.app.shared.exceptions;

import microservice.user_service.utils.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class ValidationException extends DomainException {
    public ValidationException(String message, String errorCode) {
        super(message, HttpStatus.BAD_REQUEST, errorCode);
    }
}
