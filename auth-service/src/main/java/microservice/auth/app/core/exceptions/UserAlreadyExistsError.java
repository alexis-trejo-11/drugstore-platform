package microservice.auth.app.core.exceptions;

import microservice.auth.app.shared.exceptions.ConflictException;

public class UserAlreadyExistsError extends ConflictException {
    public UserAlreadyExistsError(String message) {
        super(message, "USER_CREDENTIALS_CONFLICT");
    }
}
