package user_service.modules.users.core.application.queries;

import user_service.modules.users.core.application.dto.UserResponse;

import java.util.UUID;

public record GetUserByIdQuery(UUID userId) implements Query<UserResponse> {
    public GetUserByIdQuery {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
    }

    public static GetUserByIdQuery of(UUID userId) {
        return new GetUserByIdQuery(userId);
    }
}
