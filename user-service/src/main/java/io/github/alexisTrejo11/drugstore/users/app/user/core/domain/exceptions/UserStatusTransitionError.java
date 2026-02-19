package io.github.alexisTrejo11.drugstore.users.app.user.core.domain.exceptions;

import libs_kernel.exceptions.BusinessRuleException;

public class UserStatusTransitionError extends BusinessRuleException {
  public UserStatusTransitionError(String message) {
    super(message, "USER_STATUS_TRANSITION_ERROR");
  }
}
