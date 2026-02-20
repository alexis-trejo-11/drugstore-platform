package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.github.alexisTrejo11.drugstore.accounts.User;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.RefreshAccessTokenCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SessionResult;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.InvalidCredentialsException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.SessionExpiredException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Token;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.TokenProvider;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.SessionService;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserServiceClient;

/**
 * RefreshAccessTokenUseCase - Handles JWT access token refresh using refresh
 * tokens
 * This DDD ApplicationService validates refresh tokens and generates new access
 * tokens
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshAccessTokenUseCase {
  private final TokenProvider tokenProvider;
  private final SessionService sessionService;
  private final UserServiceClient userServiceClient;

  /**
   * Execute the refresh token use case
   *
   * @param command the refresh access token command containing the refresh token
   * @return SessionResult containing the new access token and the same refresh
   *         token
   * @throws InvalidCredentialsException if refresh token is invalid or
   *                                     blacklisted
   * @throws SessionExpiredException     if the refresh token has expired
   */
  public SessionResult execute(RefreshAccessTokenCommand command) {
    log.info("Processing token refresh request");

    if (!tokenProvider.validateToken(command.refreshToken())) {
      log.warn("Invalid or expired refresh token provided");
      throw new SessionExpiredException("Refresh token is invalid or expired");
    }
    log.debug("Refresh token validation passed");

    if (sessionService.isBlacklisted(command.refreshToken())) {
      log.warn("Blacklisted refresh token attempt");
      throw new InvalidCredentialsException("Refresh token has been revoked");
    }
    log.debug("Refresh token is not blacklisted");

    String userId = tokenProvider.extractUserId(command.refreshToken());
    String email = tokenProvider.extractEmail(command.refreshToken());
    log.debug("Extracted user info from refresh token - userId: {}", userId);

    User user = userServiceClient.getUserById(userId);
    log.debug("User retrieved from UserServiceClient - userId: {}", user.getId());

    Token newAccessToken = tokenProvider.generateAccessToken(
        user.getId(),
        user.getEmail().value(),
        user.getRole());
    log.info("New access token generated for user: {}", user.getId());

    Token newRefreshToken = tokenProvider.generateRefreshToken(
        user.getId(),
        user.getEmail().value());
    log.debug("New refresh token generated");

    var sessionResult = SessionResult.bearer(
        user.getId(),
        newAccessToken,
        newRefreshToken);

    log.debug("Token refresh completed successfully for user: {}", user.getId());
    return sessionResult;
  }
}
