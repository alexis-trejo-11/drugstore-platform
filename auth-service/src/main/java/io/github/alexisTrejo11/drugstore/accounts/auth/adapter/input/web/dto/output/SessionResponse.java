package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.output;

import java.time.LocalDateTime;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SessionPayload;
import lombok.Builder;

@Builder
public record SessionResponse(
    TokenResponse accessToken,
    TokenResponse refreshToken,
    String userId) {
  public static SessionResponse fromResult(SessionPayload result) {
    if (result == null) {
      return null;
    }

    var builder = SessionResponse.builder()
        .userId(result.userId());

    if (result.refreshToken() != null) {
      builder.refreshToken(new TokenResponse(
          result.refreshToken().code(),
          result.refreshToken().type(),
          result.refreshToken().expiresIn().toSeconds(),
          result.refreshToken().expiresAt()));
    }
    if (result.accessToken() != null) {
      builder.accessToken(new TokenResponse(
          result.accessToken().code(),
          result.accessToken().type(),
          result.accessToken().expiresIn().toSeconds(),
          result.accessToken().expiresAt()));
    }
    return builder.build();

  }

  public record TokenResponse(
      String token,
      String type,
      Long expiresIn,
      LocalDateTime expiresAt) {
  }

}