package microservice.auth.app.auth.core.application.result;

import microservice.auth.app.User;
import microservice.auth.app.auth.core.domain.valueobjects.UserId;
import microservice.auth.app.auth.core.domain.valueobjects.UserRole;

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
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        user.getRole(),
        user.getJoinedAt(),
        requiresEmailVerification);
  }
}
