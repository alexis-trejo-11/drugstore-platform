package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions;

/**
 * Exception thrown when a session cannot be found.
 * Used when attempting to retrieve or validate a non-existent session.
 */
public class SessionNotFoundException extends AuthenticationException {

  public SessionNotFoundException(String message) {
    super(message);
  }

  /**
   * Creates exception for user session not found
   */
  public static SessionNotFoundException forUser(String userId) {
    return new SessionNotFoundException(
        String.format("No active session found for user: %s", userId));
  }

  /**
   * Creates exception for refresh token not found
   */
  public static SessionNotFoundException forToken() {
    return new SessionNotFoundException(
        "Session associated with this refresh token not found");
  }

  /**
   * Creates exception for failed session blacklist operation
   */
  public static SessionNotFoundException forBlacklist() {
    return new SessionNotFoundException(
        "Failed to blacklist session - session not found or already invalidated");
  }
}
