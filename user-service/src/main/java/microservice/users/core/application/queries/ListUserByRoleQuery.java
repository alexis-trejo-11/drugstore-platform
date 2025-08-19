package microservice.users.core.application.queries;

import microservice.users.core.application.dto.UserPaginatedResponse;
import microservice.users.utils.page.PageInput;

import javax.management.relation.Role;

public record ListUserByRoleQuery(
        Role role,
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
