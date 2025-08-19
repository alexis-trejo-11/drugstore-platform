package microservice.users.core.application.queries;

import microservice.users.core.application.dto.UserPaginatedResponse;
import microservice.users.core.domain.models.enums.UserStatus;
import microservice.users.utils.page.PageInput;

import javax.management.relation.Role;

public record SearchsUserQuery(
        UserStatus status,
        Role role,
        String emailLike,
        String phoneNumberLike,
        String firstNameLike,
        String lastNameLike,
        String joinedAtFrom,
        String joinedAtTo,
        String lastLoginAtFrom,
        String lastLoginAtTo,
        PageInput pageInput
) implements Query<UserPaginatedResponse> {
    public SearchsUserQuery {
        if (pageInput == null) {
            pageInput = PageInput.defaultPageInput();
        }
    }
}
