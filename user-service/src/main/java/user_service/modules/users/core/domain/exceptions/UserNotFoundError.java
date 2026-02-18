package user_service.modules.users.core.domain.exceptions;

import user_service.utils.exceptions.NotFoundException;

import java.util.UUID;

import user_service.modules.users.core.domain.models.valueobjects.Email;
import user_service.modules.users.core.domain.models.valueobjects.PhoneNumber;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

public class UserNotFoundError extends NotFoundException {
  public UserNotFoundError(UUID id) {
    super("User", id.toString());
  }

  public UserNotFoundError(Email email) {
    super("User [Email]", email.value());
  }

  public UserNotFoundError(PhoneNumber phoneNumber) {
    super("User [PhoneNumber]", phoneNumber.value());
  }

  public UserNotFoundError(String id) {
    super("User", id);
  }

  public UserNotFoundError(UserId id) {
    super("User", id != null ? id.value() : "null");
  }
}
