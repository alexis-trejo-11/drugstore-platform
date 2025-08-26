package user_service.modules.profile.core.domain.exception;

import java.util.UUID;

import user_service.utils.exceptions.NotFoundException;

public class UserProfileNotFoundError extends NotFoundException {
    public UserProfileNotFoundError(UUID userId) {
        super("User Profile", userId.toString());
    }
}
