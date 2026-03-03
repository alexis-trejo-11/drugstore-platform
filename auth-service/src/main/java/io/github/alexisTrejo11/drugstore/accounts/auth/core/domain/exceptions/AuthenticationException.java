package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions;

import libs_kernel.exceptions.UnauthorizedException;

/**
 * Base exception for authentication-related errors.
 * Extends UnauthorizedException to maintain consistent error handling across
 * the application.
 */
public class AuthenticationException extends UnauthorizedException {
  public AuthenticationException(String message) {
    super(message);
  }
}
