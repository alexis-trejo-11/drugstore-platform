package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security;

import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.tokens.TokenType;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.tokens.factory.TokenFactory;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Token;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.TokenService;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.TokenRepository;
import libs_kernel.security.dto.UserClaims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TokenManager - Central implementation of TokenService.
 * Manages all token types: ACCESS, REFRESH (JWT) and ACTIVATION, TWO_FA
 * (numeric).
 * Uses TokenFactory for creation and TokenRepository for persistence of non-JWT
 * tokens.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenManager implements TokenService {
  private final TokenFactory tokenFactory;
  private final TokenRepository tokenRepository;

  @Override
  public Token generateToken(TokenType tokenType, UserClaims userClaims) {
    log.debug("Generating {} token for user: {}", tokenType, userClaims.userId());

    Token token = tokenFactory.createToken(tokenType, userClaims);

    // Persist non-JWT tokens to repository for later validation
    if (tokenType == TokenType.ACTIVATION || tokenType == TokenType.TWO_FA) {
      tokenRepository.create(token);
      log.info("Persisted {} token for user: {}", tokenType, userClaims.userId());
    }

    return token;
  }

  /**
   * Retrieve a token by its code.
   * For JWT tokens, validates and reconstructs from the token string.
   * For non-JWT tokens, retrieves from repository.
   * 
   * @param tokenCode the token code/string
   * @return Token record or null if not found/invalid
   */
  @Override
  public Token getToken(String tokenCode) {
    if (tokenCode == null || tokenCode.isBlank()) {
      log.warn("Attempted to get token with null or blank code");
      return null;
    }

    // Try to retrieve from repository (for non-JWT tokens)
    Optional<Token> tokenOpt = tokenRepository.get(tokenCode);
    if (tokenOpt.isPresent()) {
      Token token = tokenOpt.get();
      if (!token.isExpired()) {
        log.debug("Retrieved {} token from repository", token.type());
        return token;
      } else {
        log.debug("Token expired, deleting: {}", token.type());
        tokenRepository.delete(tokenCode);
        return null;
      }
    }

    // For JWT tokens, validate and extract claims
    if (tokenFactory.validateJwtToken(tokenCode)) {
      try {
        UserClaims claims = tokenFactory.extractClaimsFromJwt(tokenCode);
        log.debug("Retrieved JWT token for user: {}", claims.userId());
        // Note: We don't reconstruct full Token record for JWTs here
        // as expiration info would need to be extracted from JWT claims
        return null; // Or reconstruct if needed
      } catch (Exception e) {
        log.debug("Failed to extract claims from JWT", e);
      }
    }

    log.debug("Token not found: {}", maskToken(tokenCode));
    return null;
  }

  /**
   * Validate a token by type.
   * JWT tokens are validated cryptographically.
   * Non-JWT tokens are validated by repository lookup and expiration check.
   * 
   * @param tokenCode the token code
   * @param tokenType the expected token type
   * @return true if valid, false otherwise
   */
  @Override
  public boolean validateToken(String tokenCode, TokenType tokenType) {
    if (tokenCode == null || tokenCode.isBlank()) {
      log.warn("Token validation failed: null or blank token");
      return false;
    }

    log.debug("Validating {} token", tokenType);

    switch (tokenType) {
      case ACCESS, REFRESH -> {
        // Validate JWT cryptographically
        return tokenFactory.validateJwtToken(tokenCode);
      }
      case ACTIVATION, TWO_FA -> {
        // Validate numeric token from repository
        Optional<Token> tokenOpt = tokenRepository.get(tokenCode);
        if (tokenOpt.isEmpty()) {
          log.debug("Token not found in repository");
          return false;
        }

        Token token = tokenOpt.get();

        // Verify type matches
        if (!token.type().equals(tokenType.name())) {
          log.warn("Token type mismatch: expected {}, got {}", tokenType, token.type());
          return false;
        }

        // Check expiration
        if (token.isExpired()) {
          log.debug("Token expired, deleting");
          tokenRepository.delete(tokenCode);
          return false;
        }

        log.debug("Token validated successfully");
        return true;
      }
      default -> {
        log.warn("Unknown token type: {}", tokenType);
        return false;
      }
    }
  }

  /**
   * Extract user claims from a JWT token.
   * Only works for JWT tokens (ACCESS, REFRESH).
   * 
   * @param tokenCode the JWT token string
   * @return UserClaims extracted from the token
   * @throws RuntimeException if token is invalid or not a JWT
   */
  @Override
  public UserClaims extractClaims(String tokenCode) {
    if (tokenCode == null || tokenCode.isBlank()) {
      throw new IllegalArgumentException("Token cannot be null or blank");
    }

    log.debug("Extracting claims from JWT token");
    return tokenFactory.extractClaimsFromJwt(tokenCode);
  }

  /**
   * Invalidate a token by deleting it from the repository.
   * Only applicable to non-JWT tokens (ACTIVATION, TWO_FA).
   * For JWT tokens, use SessionRepository blacklist methods.
   * 
   * @param tokenCode the token code to invalidate
   */
  public void invalidateToken(String tokenCode) {
    if (tokenCode == null || tokenCode.isBlank()) {
      log.warn("Attempted to invalidate null or blank token");
      return;
    }

    log.info("Invalidating token: {}", maskToken(tokenCode));
    tokenRepository.delete(tokenCode);
  }

  /**
   * Invalidate a token for a specific user (with ownership verification).
   * 
   * @param tokenCode the token code
   * @param userId    the user ID
   */
  public void invalidateToken(String tokenCode, String userId) {
    if (tokenCode == null || tokenCode.isBlank() || userId == null || userId.isBlank()) {
      log.warn("Attempted to invalidate token with null/blank parameters");
      return;
    }

    log.info("Invalidating token for user: {}", userId);
    tokenRepository.delete(tokenCode, userId);
  }

  /**
   * Masks a token for secure logging.
   */
  private String maskToken(String token) {
    if (token == null || token.length() < 8) {
      return "***";
    }
    return token.substring(0, 4) + "..." + token.substring(token.length() - 4);
  }
}
