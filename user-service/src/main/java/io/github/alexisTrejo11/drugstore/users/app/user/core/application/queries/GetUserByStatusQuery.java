package io.github.alexisTrejo11.drugstore.users.app.user.core.application.queries;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.alexisTrejo11.drugstore.users.app.user.core.application.result.UserQueryResult;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.enums.UserStatus;

public record GetUserByStatusQuery(
    UserStatus status,
    Pageable pageInput) implements Query<Page<UserQueryResult>> {
  public GetUserByStatusQuery {
    if (status == null) {
      throw new IllegalArgumentException("User status cannot be null");
    }
    if (pageInput == null) {
      pageInput = Pageable.unpaged();
    }
  }
}
