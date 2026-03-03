package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions;

/**
 * Exception thrown when a token is expired.
 * Used for both JWT tokens and temporary tokens (2FA, password reset, etc.).
 */
public class TokenExpiredException extends AuthenticationException {

  public TokenExpiredException(String message) {
    super(message);
  }

  /**
   * Creates exception for expired JWT token
   */
  public static TokenExpiredException jwt(String tokenType) {
    return new TokenExpiredException(
        String.format("JWT %s token has expired", tokenType));
  }

  /**
   * Creates exception for expired temporary token (2FA, password reset, etc.)
   */
  public static TokenExpiredException temporary(String tokenType) {
    return new TokenExpiredException(
        String.format("%s token has expired. Please request a new one", tokenType));
  }
}
