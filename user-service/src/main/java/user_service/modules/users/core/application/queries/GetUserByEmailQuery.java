package user_service.modules.users.core.application.queries;

import user_service.modules.users.core.application.dto.UserResponse;

public record GetUserByEmailQuery(String email) implements Query<UserResponse> {
    public GetUserByEmailQuery {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
    }
}
