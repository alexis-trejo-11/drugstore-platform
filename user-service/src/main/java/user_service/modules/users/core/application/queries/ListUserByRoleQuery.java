package user_service.modules.users.core.application.queries;

import user_service.modules.users.core.application.result.UserPaginatedResponse;
import user_service.modules.users.core.domain.models.enums.UserRole;
import user_service.utils.page.PageInput;

public record ListUserByRoleQuery(
        UserRole role,
        PageInput pageInput) implements Query<UserPaginatedResponse> {
    public ListUserByRoleQuery {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        if (pageInput == null) {
            pageInput = PageInput.defaultPageInput();
        }
    }
}
