package microservice.auth_service.app.auth.core.application.result;

import microservice.auth_service.app.User;
import microservice.auth_service.app.auth.core.domain.valueobjects.UserId;
import microservice.auth_service.app.auth.core.domain.valueobjects.UserRole;

public record SignUpResult(
    UserId userId,
    String email,
    String firstName,
    String lastName,
    UserRole role,
    java.time.LocalDateTime createdAt,
    Boolean requiresEmailVerification) {

  public static SignUpResult success(User user, Boolean requiresEmailVerification) {
    return new SignUpResult(
        user.getId(),
        user.getEmail() != null ? user.getEmail().value() : null,
        user.getFirstName(),
        user.getLastName(),
        user.getRole(),
        user.getJoinedAt(),
        requiresEmailVerification);
  }
}
