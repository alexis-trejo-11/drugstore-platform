package microservice.user_service.users.core.application.queries;

import microservice.user_service.users.core.application.dto.UserResponse;
import microservice.user_service.users.core.domain.models.valueobjects.PhoneNumber;

public record GetUserByPhoneNumberQuery(PhoneNumber phoneNumber) implements Query<UserResponse> {
    public GetUserByPhoneNumberQuery {
        if (phoneNumber == null) {
            throw new IllegalArgumentException("Phone Number cannot be null");
        }
    }
}
