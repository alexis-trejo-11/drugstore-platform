package user_service.modules.users.core.application.queries;

import user_service.modules.users.core.application.result.UserPaginatedResponse;
import user_service.modules.users.core.domain.models.enums.UserStatus;
import user_service.utils.page.PageInput;

public record ListUserByStatusQuery(
        UserStatus status,
        PageInput pageInput) implements Query<UserPaginatedResponse> {
    public ListUserByStatusQuery {
        if (status == null) {
            throw new IllegalArgumentException("User status cannot be null");
        }
        if (pageInput == null) {
            pageInput = PageInput.defaultPageInput();
        }
    }
}
