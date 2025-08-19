package microservice.users.core.application.queries;

import microservice.users.core.application.dto.UserPaginatedResponse;
import microservice.users.core.domain.models.enums.UserStatus;
import microservice.users.utils.page.PageInput;

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
