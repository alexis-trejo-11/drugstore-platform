package user_service.modules.users.core.application.queries;

import user_service.modules.users.core.application.result.UserQueryResult;
import user_service.modules.users.core.domain.models.valueobjects.Email;

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
