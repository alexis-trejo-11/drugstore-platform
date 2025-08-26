package user_service.utils.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends DomainException {
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "PH-401");
    }
}
