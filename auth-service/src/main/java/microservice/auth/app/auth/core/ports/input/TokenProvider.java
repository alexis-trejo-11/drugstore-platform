package microservice.auth.app.auth.core.ports.input;

import java.util.Map;

import microservice.auth.app.auth.core.domain.valueobjects.Token;
import microservice.auth.app.auth.core.domain.valueobjects.UserId;
import microservice.auth.app.auth.core.domain.valueobjects.UserRole;

/**
 * Port for JWT token generation and validation.
 * Implementation should handle JWT token creation and validation with
 * appropriate expiry times.
 */
public interface TokenProvider {
  /**
   * Generate an access token for the given user
   * 
   * @param userId the user ID
   * @param email  the user email
   * @param role   the user role
   * @return the generated access token
   */
  Token generateAccessToken(UserId userId, String email, UserRole role);

  /**
   * Generate a refresh token for the given user
   * 
   * @param userId the user ID
   * @param email  the user email
   * @return the generated refresh token
   */
  Token generateRefreshToken(UserId userId, String email);

  /**
   * Validate a JWT token
   * 
   * @param token the JWT token string
   * @return true if valid, false otherwise
   */
  boolean validateToken(String token);

  /**
   * Extract user ID from a JWT token
   * 
   * @param token the JWT token string
   * @return the user ID if found
   */
  String extractUserId(String token);

  /**
   * Extract email from a JWT token
   * 
   * @param token the JWT token string
   * @return the email if found
   */
  String extractEmail(String token);

  /**
   * Extract claims from a JWT token
   * 
   * @param token the JWT token string
   * @return map of claims
   */
  Map<String, Object> extractClaims(String token);
}
