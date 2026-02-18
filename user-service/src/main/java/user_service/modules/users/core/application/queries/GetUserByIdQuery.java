package user_service.modules.users.core.application.queries;

import user_service.modules.users.core.application.result.UserQueryResult;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

public record GetUserByIdQuery(UserId userId) implements Query<UserQueryResult> {
  public GetUserByIdQuery {
    if (userId == null) {
      throw new IllegalArgumentException("UserId cannot be null");
    }
  }

  public static GetUserByIdQuery of(String userId) {
    return new GetUserByIdQuery(new UserId(userId));
  }
}
