package microservice.user_service.users.core.application.queries;

import microservice.user_service.users.core.application.dto.UserPaginatedResponse;
import microservice.user_service.users.core.domain.models.enums.UserRole;
import microservice.user_service.utils.page.PageInput;

public record ListUserByRoleQuery(
        UserRole role,
        PageInput pageInput
) implements Query<UserPaginatedResponse> {
    public ListUserByRoleQuery {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        if (pageInput == null) {
            pageInput = PageInput.defaultPageInput();
        }
    }
}
