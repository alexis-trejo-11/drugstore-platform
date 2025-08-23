package microservice.auth.app.shared.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends DomainException {
    public ConflictException(String message, String errorCode) {
        super(message, HttpStatus.CONFLICT, errorCode);
    }
}
