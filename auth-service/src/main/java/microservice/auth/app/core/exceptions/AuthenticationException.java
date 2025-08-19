package microservice.auth.app.core.exceptions;

import microservice.auth.app.shared.exceptions.UnauthorizedException;

public class AuthenticationException extends UnauthorizedException {
    public AuthenticationException(String message) {
        super(message);
    }
}
