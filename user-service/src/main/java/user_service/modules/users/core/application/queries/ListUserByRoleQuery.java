package user_service.modules.users.core.application.queries;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import user_service.modules.users.core.application.result.UserQueryResult;
import user_service.modules.users.core.domain.models.enums.UserRole;

public record ListUserByRoleQuery(
		UserRole role,
		Pageable pageInput) implements Query<Page<UserQueryResult>> {
	public ListUserByRoleQuery {
		if (role == null) {
			throw new IllegalArgumentException("Role cannot be null");
		}
		if (pageInput == null) {
			pageInput = Pageable.unpaged();
		}
	}
}
