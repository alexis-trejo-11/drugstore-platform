package microservice.auth.app.auth.core.domain.exceptions;

import microservice.auth.app.shared.exceptions.UnauthorizedException;

public class SessionExpiredException extends UnauthorizedException {
    public SessionExpiredException(String message) {
        super(message);
    }
}
