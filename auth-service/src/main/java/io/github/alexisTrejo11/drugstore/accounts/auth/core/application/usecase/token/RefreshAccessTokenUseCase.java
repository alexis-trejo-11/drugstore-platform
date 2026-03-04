package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase;

import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.tokens.TokenType;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SessionPayload;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.TokenService;
import libs_kernel.security.dto.UserClaims;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.RefreshAccessTokenCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.InvalidTokenException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.TokenExpiredException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Token;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.SessionRepository;

/**
 * RefreshAccessTokenUseCase - Handles JWT access token refresh using refresh
 * tokens.
 * This DDD ApplicationService validates refresh tokens and generates new access
 * tokens
 * while maintaining session security through blacklist checks.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshAccessTokenUseCase {
  private final TokenService tokenService;
  private final SessionRepository sessionRepository;

  /**
   * Execute the refresh token use case.
   *
   * @param command the refresh access token command containing the refresh token
   * @return SessionPayload containing the new access token and the same refresh
   *         token
   * @throws InvalidTokenException if refresh token is invalid or blacklisted
   * @throws TokenExpiredException if the refresh token has expired
   */
  public SessionPayload execute(RefreshAccessTokenCommand command) {
    log.info("Processing token refresh request");

    validateRefreshToken(command.refreshToken());
    checkTokenNotBlacklisted(command.refreshToken());

    UserClaims claims = extractClaimsFromToken(command.refreshToken());

    Token newAccessToken = tokenService.generateToken(TokenType.ACCESS, claims);
    Token currentRefreshToken = tokenService.getToken(command.refreshToken());

    log.info("Token refresh completed successfully for user: {}", claims.userId());

    return SessionPayload.bearer(
        claims.userId(),
        newAccessToken,
        currentRefreshToken);
  }

  /**
   * Validates that the refresh token is valid and not expired.
   */
  private void validateRefreshToken(String refreshToken) {
    log.debug("Validating refresh token");

    if (!tokenService.validateToken(refreshToken, TokenType.REFRESH)) {
      log.warn("Invalid or expired refresh token provided");
      throw TokenExpiredException.jwt("refresh");
    }

    log.debug("Refresh token validation passed");
  }

  /**
   * Checks that the refresh token is not blacklisted.
   */
  private void checkTokenNotBlacklisted(String refreshToken) {
    log.debug("Checking if refresh token is blacklisted");

    if (sessionRepository.isBlacklisted(refreshToken)) {
      log.warn("Blacklisted refresh token attempt detected");
      throw InvalidTokenException.blacklisted();
    }

    log.debug("Refresh token is not blacklisted");
  }

  /**
   * Extracts user claims from the refresh token.
   */
  private UserClaims extractClaimsFromToken(String refreshToken) {
    try {
      log.debug("Extracting claims from refresh token");
      UserClaims claims = tokenService.extractClaims(refreshToken);
      log.debug("Successfully extracted claims for user: {}", claims.userId());
      return claims;
    } catch (Exception e) {
      log.error("Failed to extract claims from refresh token: {}", e.getMessage());
      throw InvalidTokenException.malformed("Refresh");
    }
  }
}
