package io.github.alexisTrejo11.drugstore.order.orders.domain.exceptions;

import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;
import libs_kernel.exceptions.NotFoundException;

public class UserNotFoundException extends NotFoundException {
  public UserNotFoundException(UserID userId) {
    super("User", "id", userId.value());
  }
}
