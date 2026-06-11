package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Token;

public record SessionPayload(
    Token accessToken,
    Token refreshToken,
    String userId,
    String tokenType,
    boolean requiresTwoFactor) {

  public static SessionPayload bearer(String userId, Token accessToken, Token refreshToken) {
    return new SessionPayload(accessToken, refreshToken, userId, "Bearer", false);
  }

  /**
   * Password accepted; 2FA code was sent out-of-band — client must complete
   * {@code /login/2fa}.
   */
  public static SessionPayload pendingTwoFactor(String userId) {
    return new SessionPayload(null, null, userId, "Bearer", true);
  }
}
