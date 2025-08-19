package microservice.users.core.domain.exceptions;

import microservice.users.utils.exceptions.NotFoundException;

public class UserNotFoundError extends NotFoundException {
    public UserNotFoundError(String identifier) {
        super("User", identifier);
    }
}
