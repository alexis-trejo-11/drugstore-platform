package user_service.modules.users.core.application.queries;

import java.util.UUID;

import user_service.modules.users.core.application.result.UserResponse;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

public record GetUserByIdQuery(UserId userId) implements Query<UserResponse> {
    public GetUserByIdQuery {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
    }

    public static GetUserByIdQuery of(String userId) {
        return new GetUserByIdQuery(new UserId(userId));
    }
}
