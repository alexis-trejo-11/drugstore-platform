package user_service.modules.users.core.application.result;

import user_service.utils.page.PaginationMetadata;

import java.util.List;

public record UserPaginatedResponse(List<UserResponse> users, PaginationMetadata pageMetadata) {
    public UserPaginatedResponse {
        if (users == null) {
            users = List.of();
        }
        if (pageMetadata == null) {
            pageMetadata = PaginationMetadata.empty();
        }
    }
}
