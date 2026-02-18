package user_service.modules.profile.core.domain.exception;

import libs_kernel.exceptions.NotFoundException;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

public class UserProfileNotFoundError extends NotFoundException {
  public UserProfileNotFoundError(UserId userId) {
    super("User Profile", "Id", userId != null ? userId.value() : "null");
  }
}
