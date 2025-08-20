package microservice.user_service.users.core.application.queries;

import microservice.user_service.users.core.application.dto.UserPaginatedResponse;
import microservice.user_service.users.core.domain.models.enums.UserStatus;
import microservice.user_service.utils.page.PageInput;

import javax.management.relation.Role;

public record SearchUserQuery(
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
    public SearchUserQuery {
        if (pageInput == null) {
            pageInput = PageInput.defaultPageInput();
        }
    }

    public String toJson() {
        return String.format(
                "{\"status\":\"%s\", \"role\":\"%s\", \"emailLike\":\"%s\", \"phoneNumberLike\":\"%s\", \"firstNameLike\":\"%s\", \"lastNameLike\":\"%s\", \"joinedAtFrom\":\"%s\", \"joinedAtTo\":\"%s\", \"lastLoginAtFrom\":\"%s\", \"lastLoginAtTo\":\"%s\", \"pageInput\":%s}",
                status, role, emailLike, phoneNumberLike, firstNameLike, lastNameLike,
                joinedAtFrom, joinedAtTo, lastLoginAtFrom, lastLoginAtTo, pageInput.toJson()
        );
    }
}
