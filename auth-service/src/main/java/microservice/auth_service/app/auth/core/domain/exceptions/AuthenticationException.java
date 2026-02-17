package microservice.auth_service.app.auth.core.domain.exceptions;

import libs_kernel.exceptions.UnauthorizedException;

public class AuthenticationException extends UnauthorizedException {
    public AuthenticationException(String message) {
        super(message);
    }
}
