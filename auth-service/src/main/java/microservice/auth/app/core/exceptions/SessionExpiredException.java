package microservice.auth.app.core.exceptions;

import microservice.auth.app.shared.exceptions.UnauthorizedException;

public class SessionExpiredException extends UnauthorizedException {
    public SessionExpiredException(String message) {
        super(message);
    }
}
