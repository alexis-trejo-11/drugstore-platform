package microservice.auth.app.auth.core.domain.exceptions;

import libs_kernel.exceptions.UnauthorizedException;

/**
 * Exception thrown when user credentials are invalid during login.
 * This follows DDD principles by being a domain exception.
 */
public class InvalidCredentialsException extends UnauthorizedException {
  public InvalidCredentialsException(String message) {
    super(message);
  }
}
