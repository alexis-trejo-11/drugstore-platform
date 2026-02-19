package io.github.alexisTrejo11.drugstore.users.app.user.core.application.queries;

import io.github.alexisTrejo11.drugstore.users.app.user.core.application.result.UserQueryResult;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.Email;

public record GetUserByEmailQuery(Email email) implements Query<UserQueryResult> {
	public GetUserByEmailQuery {
		if (email == null) {
			throw new IllegalArgumentException("Email cannot be null");
		}
	}

	public static GetUserByEmailQuery of(String email) {
		return new GetUserByEmailQuery(new Email(email));
	}
}
