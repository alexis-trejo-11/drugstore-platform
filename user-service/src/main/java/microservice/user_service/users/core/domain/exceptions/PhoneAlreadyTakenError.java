package microservice.user_service.users.core.domain.exceptions;

import microservice.user_service.utils.exceptions.ConflictException;

public class PhoneAlreadyTakenError extends ConflictException {
    public PhoneAlreadyTakenError(String value) {
        super("Phone Already Taken: " + value, "PHONE_ALREADY_TAKEN");
    }
}
