package io.github.alexisTrejo11.drugstore.users.app.user.core.application.queries;

import io.github.alexisTrejo11.drugstore.users.app.user.core.application.result.UserQueryResult;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.UserId;

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
