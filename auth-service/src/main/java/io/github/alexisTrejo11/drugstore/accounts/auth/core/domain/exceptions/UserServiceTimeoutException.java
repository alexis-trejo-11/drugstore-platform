package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions;

import libs_kernel.exceptions.DomainException;

public class UserServiceTimeoutException extends DomainException {
  public UserServiceTimeoutException(String message) {
    super(message, null, "USER-SERVICE-TIMEOUT");
  }

}
