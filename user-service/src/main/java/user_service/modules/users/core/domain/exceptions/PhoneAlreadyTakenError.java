package user_service.modules.users.core.domain.exceptions;

import user_service.modules.users.core.domain.models.valueobjects.PhoneNumber;
import user_service.utils.exceptions.ConflictException;

public class PhoneAlreadyTakenError extends ConflictException {
    public PhoneAlreadyTakenError(PhoneNumber value) {
        super("Phone Already Taken: " + (value != null ? value.value() : "null"), "PHONE_ALREADY_TAKEN");
    }

}
