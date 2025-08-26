package user_service.modules.users.core.domain.exceptions;

import user_service.utils.exceptions.ConflictException;

public class EmailAlreadyTakenError extends ConflictException {
    public EmailAlreadyTakenError(String value) {
        super("Email already taken: " + value, "EMAIL_ALREADY_TAKEN");
    }
}
