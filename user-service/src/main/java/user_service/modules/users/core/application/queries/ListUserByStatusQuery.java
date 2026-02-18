package user_service.modules.users.core.application.queries;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import user_service.modules.users.core.application.result.UserQueryResult;
import user_service.modules.users.core.domain.models.enums.UserStatus;

public record ListUserByStatusQuery(
    UserStatus status,
    Pageable pageInput) implements Query<Page<UserQueryResult>> {
  public ListUserByStatusQuery {
    if (status == null) {
      throw new IllegalArgumentException("User status cannot be null");
    }
    if (pageInput == null) {
      pageInput = Pageable.unpaged();
    }
  }
}
