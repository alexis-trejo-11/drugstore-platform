package io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input;


import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.tokens.TokenType;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Token;
import libs_kernel.security.dto.UserClaims;

/**
 * Port for JWT token generation and validation.
 * Implementation should handle JWT token creation and validation with
 * appropriate expiry times.
 */
public interface TokenService {
  /**
   * Generate an access token for the given user
   * 
   * @param tokenType the type of token to generate (ACCESS or REFRESH or ACTIVATION or TWO_FACTOR)
	 * @param userClaims the claims to include in the token (userId, role, email)
	 * @return generated Token object containing the token string and metadata
   */
  Token generateToken(TokenType tokenType, UserClaims userClaims);

	/**
	 * Retrieve a Token object from a token string
	 *
	 * @param token the JWT token string
	 * @return Token object containing the token string and metadata
	 */
	Token getToken(String token);

  /**
   * Validate a JWT token
   * 
   * @param token the JWT token string
   * @return true if valid, false otherwise
   */
  boolean validateToken(String token, TokenType tokenType);

  /**
   * Extract claims from a JWT token
   * 
   * @param token the JWT token string
   * @return map of claims
   */
  UserClaims extractClaims(String token);

	/**
	 * Invalidate a token (e.g. on logout)
	 *
	 * @param token the JWT token string to invalidate
	 */
	void invalidateToken(String token);
}
