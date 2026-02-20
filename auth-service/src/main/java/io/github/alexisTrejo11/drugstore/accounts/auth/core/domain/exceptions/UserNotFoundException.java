package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions;

import libs_kernel.exceptions.NotFoundException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;

public class UserNotFoundException extends NotFoundException {
  public UserNotFoundException(UserId userId) {
    super("User", "ID", userId.value());
  }

  public UserNotFoundException(String message) {
    super(message);
  }

}
