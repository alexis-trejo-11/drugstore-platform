package microservice.users.core.application.queries;

import microservice.users.core.application.dto.UserResponse;
import microservice.users.core.domain.models.valueobjects.Email;


public record GetUserByEmailQuery(Email email) implements Query<UserResponse> {
    public GetUserByEmailQuery {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
    }
}
