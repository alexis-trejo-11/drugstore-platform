package user_service.modules.users.adapter.input.rest.dto;

import lombok.Builder;
import user_service.modules.users.core.application.result.UserResponse;
import user_service.modules.users.core.domain.models.enums.UserRole;

@Builder
public record UserHTTPResponse(
    String id,
    String email,
    String phoneNumber,
    UserRole role,
    String joinedAt,
    String lastLoginAt) {

  public static UserHTTPResponse from(UserResponse user) {
    return new UserHTTPResponse(
        user.id() != null ? user.id().toString() : null,
        user.email() != null ? user.email().value() : null,
        user.phoneNumber() != null ? user.phoneNumber().value() : null,
        user.role(),
        user.createdAt() != null ? user.createdAt().toString() : null,
        user.lastLoginAt() != null ? user.lastLoginAt().toString() : null);
  }
}
