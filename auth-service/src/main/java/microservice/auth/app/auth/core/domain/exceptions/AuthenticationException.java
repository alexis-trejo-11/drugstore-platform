package microservice.auth.app.auth.core.domain.exceptions;

import microservice.auth.app.shared.exceptions.UnauthorizedException;

public class AuthenticationException extends UnauthorizedException {
    public AuthenticationException(String message) {
        super(message);
    }
}
