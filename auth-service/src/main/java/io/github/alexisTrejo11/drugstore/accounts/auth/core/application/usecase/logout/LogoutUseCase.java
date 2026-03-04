package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.logout;

import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.tokens.TokenType;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.SessionRepository;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.TokenService;
import libs_kernel.security.dto.UserClaims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.LogoutCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.InvalidTokenException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.SessionNotFoundException;
import org.springframework.stereotype.Service;

/**
 * LogoutUseCase - Handles user logout by invalidating the refresh token.
 * This DDD ApplicationService blacklists the refresh token permanently to
 * prevent reuse.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutUseCase {
  private final SessionRepository sessionRepository;
  private final TokenService tokenService;

  /**
   * Execute the logout use case.
   *
   * @param command the logout command containing the refresh token
   * @throws InvalidTokenException    if the refresh token is invalid or malformed
   * @throws SessionNotFoundException if the session cannot be invalidated
   */
  public void execute(LogoutCommand command) {
    log.info("Processing user logout request");

    validateRefreshToken(command.refreshToken());
    UserClaims claims = extractClaimsFromToken(command.refreshToken());
    blacklistRefreshToken(command.refreshToken());

    log.info("User {} logged out successfully - refresh token blacklisted", claims.userId());
  }

  /**
   * Validates that the refresh token is valid.
   */
  private void validateRefreshToken(String refreshToken) {
    log.debug("Validating refresh token for logout");

    if (!tokenService.validateToken(refreshToken, TokenType.REFRESH)) {
      log.warn("Invalid refresh token provided for logout");
      throw InvalidTokenException.malformed("Refresh");
    }

    log.debug("Refresh token validation passed");
  }

  /**
   * Extracts user claims from the refresh token.
   */
  private UserClaims extractClaimsFromToken(String refreshToken) {
    try {
      log.debug("Extracting claims from refresh token");
      UserClaims claims = tokenService.extractClaims(refreshToken);
      log.debug("User ID extracted from token: {}", claims.userId());
      return claims;
    } catch (Exception e) {
      log.error("Failed to extract claims from refresh token: {}", e.getMessage());
      throw InvalidTokenException.malformed("Refresh");
    }
  }

  /**
   * Blacklists a refresh token in the session repository.
   * This prevents the token from being used for further token refreshes.
   *
   * @param refreshToken the refresh token to blacklist
   * @throws SessionNotFoundException if blacklisting fails
   */
  private void blacklistRefreshToken(String refreshToken) {
    try {
      log.debug("Blacklisting refresh token");
      sessionRepository.blacklistRefreshToken(refreshToken);
      log.debug("Refresh token successfully blacklisted");
    } catch (Exception e) {
      log.error("Failed to blacklist refresh token: {}", e.getMessage(), e);
      throw SessionNotFoundException.forBlacklist();
    }
  }
}
