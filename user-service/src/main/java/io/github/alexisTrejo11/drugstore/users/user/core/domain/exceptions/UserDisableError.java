package io.github.alexisTrejo11.drugstore.users.user.core.domain.exceptions;

import libs_kernel.exceptions.BusinessRuleException;

public class UserDisableError extends BusinessRuleException {
  public UserDisableError(String message) {
    super(message, "USER_DISABLED");
  }

}
