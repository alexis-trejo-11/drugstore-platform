package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions;

/**
 * Exception thrown when a token is malformed, invalid, or cannot be processed.
 * Different from TokenExpiredException which is specifically for expired
 * tokens.
 */
public class InvalidTokenException extends AuthenticationException {

  public InvalidTokenException(String message) {
    super(message);
  }

  /**
   * Creates exception for malformed token
   */
  public static InvalidTokenException malformed(String tokenType) {
    return new InvalidTokenException(
        String.format("%s token is malformed or invalid", tokenType));
  }

  /**
   * Creates exception for blacklisted token
   */
  public static InvalidTokenException blacklisted() {
    return new InvalidTokenException("Token has been revoked or blacklisted");
  }

  /**
   * Creates exception for token not found
   */
  public static InvalidTokenException notFound(String tokenType) {
    return new InvalidTokenException(
        String.format("%s token not found or does not exist", tokenType));
  }
}
