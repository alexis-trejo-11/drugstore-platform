package user_service.modules.users.core.domain.exceptions;

import user_service.utils.exceptions.ConflictException;

public class PhoneAlreadyTakenError extends ConflictException {
    public PhoneAlreadyTakenError(String value) {
        super("Phone Already Taken: " + value, "PHONE_ALREADY_TAKEN");
    }
}
