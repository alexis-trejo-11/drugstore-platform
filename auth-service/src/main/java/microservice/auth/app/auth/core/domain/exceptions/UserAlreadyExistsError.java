package microservice.auth.app.auth.core.domain.exceptions;

import microservice.auth.app.shared.exceptions.ConflictException;

public class UserAlreadyExistsError extends ConflictException {
    public UserAlreadyExistsError(String message) {
        super(message, "USER_CREDENTIALS_CONFLICT");
    }
}
