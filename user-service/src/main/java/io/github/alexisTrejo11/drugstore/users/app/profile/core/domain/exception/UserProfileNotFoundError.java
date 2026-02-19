package io.github.alexisTrejo11.drugstore.users.app.profile.core.domain.exception;

import libs_kernel.exceptions.NotFoundException;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.UserId;

public class UserProfileNotFoundError extends NotFoundException {
  public UserProfileNotFoundError(UserId userId) {
    super("User Profile", "Id", userId != null ? userId.value() : "null");
  }
}
