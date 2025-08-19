package microservice.users.core.domain.exceptions;

import microservice.users.utils.exceptions.ConflictException;

public class EmailAlreadyTakenError extends ConflictException {
    public EmailAlreadyTakenError(String value) {
        super("Email already taken: " + value, "EMAIL_ALREADY_TAKEN");
    }
}
