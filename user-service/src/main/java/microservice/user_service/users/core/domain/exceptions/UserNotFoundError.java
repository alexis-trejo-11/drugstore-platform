package microservice.user_service.users.core.domain.exceptions;

import microservice.user_service.utils.exceptions.NotFoundException;

import java.util.UUID;

public class UserNotFoundError extends NotFoundException {
    public UserNotFoundError(UUID id) {
        super("User", id.toString());
    }
    public UserNotFoundError(String id) {
        super("User", id);
    }
}
