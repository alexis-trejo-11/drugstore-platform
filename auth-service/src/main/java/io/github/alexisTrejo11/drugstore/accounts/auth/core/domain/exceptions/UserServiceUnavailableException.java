package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions;

public class UserServiceUnavailableException extends RuntimeException {
  public UserServiceUnavailableException(String message) {
    super(message);
  }

}
