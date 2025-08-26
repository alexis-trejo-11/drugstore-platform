package user_service.modules.auth.core.domain.exception;

import java.util.UUID;

import user_service.utils.exceptions.NotFoundException;

public class UserSessionNotFound extends NotFoundException {
    public UserSessionNotFound(UUID userId) {
        super("User Session", userId.toString());
    }

}
