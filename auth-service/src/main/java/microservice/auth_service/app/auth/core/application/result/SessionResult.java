package microservice.auth_service.app.auth.core.application.result;

import microservice.auth_service.app.auth.core.domain.valueobjects.Token;
import microservice.auth_service.app.auth.core.domain.valueobjects.UserId;

public record SessionResult(
    UserId userId,
    Token refreshToken,
    Token accessToken,
    String tokenType) {

  public static SessionResult bearer(UserId userId, Token accessToken, Token refreshToken) {
    return new SessionResult(userId, refreshToken, accessToken, "Bearer");

  }
}
