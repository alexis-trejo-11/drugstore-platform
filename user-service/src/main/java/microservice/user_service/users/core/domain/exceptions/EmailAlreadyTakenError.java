package microservice.user_service.users.core.domain.exceptions;

import microservice.user_service.utils.exceptions.ConflictException;

public class EmailAlreadyTakenError extends ConflictException {
    public EmailAlreadyTakenError(String value) {
        super("Email already taken: " + value, "EMAIL_ALREADY_TAKEN");
    }
}
