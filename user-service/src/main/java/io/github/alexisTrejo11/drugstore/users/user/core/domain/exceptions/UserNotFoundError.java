package io.github.alexisTrejo11.drugstore.users.user.core.domain.exceptions;

import java.util.UUID;

import libs_kernel.exceptions.NotFoundException;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.Email;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.PhoneNumber;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.UserId;

public class UserNotFoundError extends NotFoundException {
  public UserNotFoundError(UUID id) {
    super("User", "Id", id.toString());
  }

  public UserNotFoundError(Email email) {
    super("User", "Email", email.value());
  }

  public UserNotFoundError(PhoneNumber phoneNumber) {
    super("User", "PhoneNumber", phoneNumber.value());
  }

  public UserNotFoundError(String id) {
    super("User", "Id", id);
  }

  public UserNotFoundError(UserId id) {
    super("User", "Id", id != null ? id.value() : "null");
  }
}
