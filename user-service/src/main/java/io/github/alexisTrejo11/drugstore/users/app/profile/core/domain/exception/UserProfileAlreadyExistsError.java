package io.github.alexisTrejo11.drugstore.users.app.profile.core.domain.exception;

import libs_kernel.exceptions.ConflictException;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.UserId;

public class UserProfileAlreadyExistsError extends ConflictException {

  public UserProfileAlreadyExistsError(UserId userId) {
    super("Profile for UserId " + (userId != null ? userId.value() : "null") + " already exists",
        "PROFILE_ALREADY_EXISTS");

  }

}
