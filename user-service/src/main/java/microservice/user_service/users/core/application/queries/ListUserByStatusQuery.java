package microservice.user_service.users.core.application.queries;

import microservice.user_service.users.core.application.dto.UserPaginatedResponse;
import microservice.user_service.users.core.domain.models.enums.UserStatus;
import microservice.user_service.utils.page.PageInput;

public record ListUserByStatusQuery(
        UserStatus status,
        PageInput pageInput
) implements Query<UserPaginatedResponse> {
    public ListUserByStatusQuery {
        if (status == null) {
            throw new IllegalArgumentException("User status cannot be null");
        }
        if (pageInput == null) {
            pageInput = PageInput.defaultPageInput();
        }
    }
}
