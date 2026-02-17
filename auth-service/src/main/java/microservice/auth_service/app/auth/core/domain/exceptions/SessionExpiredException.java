package microservice.auth_service.app.auth.core.domain.exceptions;

import libs_kernel.exceptions.UnauthorizedException;

public class SessionExpiredException extends UnauthorizedException {
    public SessionExpiredException(String message) {
        super(message);
    }
}
