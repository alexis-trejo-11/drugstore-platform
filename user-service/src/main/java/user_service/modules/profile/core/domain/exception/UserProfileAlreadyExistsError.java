package user_service.modules.profile.core.domain.exception;

import java.util.UUID;

import user_service.utils.exceptions.ConflictException;

public class UserProfileAlreadyExistsError extends ConflictException {

    public UserProfileAlreadyExistsError(UUID userId) {
        super("Profile for UserId " + userId + " already exists", "PROFILE_ALREADY_EXISTS");

    }

}
