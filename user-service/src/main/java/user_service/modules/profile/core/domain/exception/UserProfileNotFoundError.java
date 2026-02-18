package user_service.modules.profile.core.domain.exception;

import user_service.modules.users.core.domain.models.valueobjects.UserId;
import user_service.utils.exceptions.NotFoundException;

public class UserProfileNotFoundError extends NotFoundException {
  public UserProfileNotFoundError(UserId userId) {
    super("User Profile", userId != null ? userId.value() : "null");
  }
}
