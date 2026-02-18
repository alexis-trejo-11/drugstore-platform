package user_service.modules.users.adapter.input.rest.dto;

import lombok.Builder;
import user_service.modules.users.core.application.result.UserQueryResult;
import user_service.modules.users.core.domain.models.enums.UserRole;

@Builder
public record UserHTTPResponse(
    String id,
    String email,
    String phoneNumber,
    UserRole role,
    String joinedAt,
    String lastLoginAt) {

  public static UserHTTPResponse from(UserQueryResult result) {
    return new UserHTTPResponse(
        result.id() != null ? result.id().toString() : null,
        result.email() != null ? result.email().value() : null,
        result.phoneNumber() != null ? result.phoneNumber().value() : null,
        result.role(),
        result.createdAt() != null ? result.createdAt().toString() : null,
        result.lastLoginAt() != null ? result.lastLoginAt().toString() : null);
  }
}
