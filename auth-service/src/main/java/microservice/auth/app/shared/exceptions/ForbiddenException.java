package microservice.auth.app.shared.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends DomainException {
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN, "PH-403");
    }
}
