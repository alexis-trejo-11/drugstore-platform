package microservice.users.core.domain.exceptions;

import microservice.users.utils.exceptions.ConflictException;

public class PhoneAlreadyTakenError extends ConflictException {
    public PhoneAlreadyTakenError(String value) {
        super("Phone Already Taken: " + value, "PHONE_ALREADY_TAKEN");
    }
}
