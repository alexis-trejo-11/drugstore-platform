package user_service.modules.users.core.application.queries;

import user_service.modules.users.core.application.result.UserResponse;
import user_service.modules.users.core.domain.models.valueobjects.Email;

public record GetUserByEmailQuery(Email email) implements Query<UserResponse> {
  public GetUserByEmailQuery {
    if (email == null) {
      throw new IllegalArgumentException("Email cannot be null");
    }
  }

  public static GetUserByEmailQuery of(String email) {
    return new GetUserByEmailQuery(new Email(email));
  }
}
