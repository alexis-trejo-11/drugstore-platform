package io.github.alexisTrejo11.drugstore.users.user.core.application.queries;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.alexisTrejo11.drugstore.users.user.core.application.result.UserQueryResult;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.UserRole;

public record GetUsersByRoleQuery(
		UserRole role,
		Pageable pageInput) implements Query<Page<UserQueryResult>> {
	public GetUsersByRoleQuery {
		if (role == null) {
			throw new IllegalArgumentException("Role cannot be null");
		}
		if (pageInput == null) {
			pageInput = Pageable.unpaged();
		}
	}
}
