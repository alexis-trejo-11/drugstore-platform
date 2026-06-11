package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.output;

import java.time.LocalDateTime;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SessionPayload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(
    description =
        "Active session payload. When `requiresTwoFactor` is true, tokens may be absent until "
            + "POST `/api/v2/auth/login/2fa` completes.")
@Builder
public record SessionResponse(
    @Schema(description = "Short-lived access JWT") TokenResponse accessToken,
    @Schema(description = "Refresh token used at `/api/v2/auth/session/refresh`")
        TokenResponse refreshToken,
    @Schema(description = "Authenticated user id", example = "usr_01JABC123")
        String userId,
    @Schema(
            description =
                "If true, login requires completing the second factor using `/api/v2/auth/login/2fa`")
        boolean requiresTwoFactor) {
  public static SessionResponse fromResult(SessionPayload result) {
    if (result == null) {
      return null;
    }

    var builder =
        SessionResponse.builder().userId(result.userId()).requiresTwoFactor(result.requiresTwoFactor());

    if (result.refreshToken() != null) {
      builder.refreshToken(
          new TokenResponse(
              result.refreshToken().code(),
              result.refreshToken().type(),
              result.refreshToken().expiresIn().toSeconds(),
              result.refreshToken().expiresAt()));
    }
    if (result.accessToken() != null) {
      builder.accessToken(
          new TokenResponse(
              result.accessToken().code(),
              result.accessToken().type(),
              result.accessToken().expiresIn().toSeconds(),
              result.accessToken().expiresAt()));
    }
    return builder.build();
  }

  @Schema(description = "Token metadata returned to API clients")
  public record TokenResponse(
      @Schema(description = "JWT or opaque token string") String token,
      @Schema(description = "Token type label", example = "Bearer") String type,
      @Schema(description = "Seconds until expiry") Long expiresIn,
      @Schema(description = "Absolute expiry timestamp") LocalDateTime expiresAt) {}
}
