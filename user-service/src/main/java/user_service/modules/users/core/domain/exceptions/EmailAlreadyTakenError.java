package user_service.modules.users.core.domain.exceptions;

import user_service.modules.users.core.domain.models.valueobjects.Email;
import user_service.utils.exceptions.ConflictException;

public class EmailAlreadyTakenError extends ConflictException {
    public EmailAlreadyTakenError(Email email) {
        super("Email already taken: " + (email != null ? email.value() : "null"), "EMAIL_ALREADY_TAKEN");
    }
}
