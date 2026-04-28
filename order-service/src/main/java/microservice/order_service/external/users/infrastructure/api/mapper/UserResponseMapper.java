package microservice.order_service.external.users.infrastructure.api.mapper;

import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.external.users.infrastructure.api.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserResponseMapper implements ResponseMapper<UserResponse, User> {
    @Override
    public UserResponse toResponse(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId() != null ? user.getId().value() : null)
                .name(user.getName() != null ? user.getName() : null)
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null)
                .email(user.getEmail() != null ? user.getEmail() : null)
                .phoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber() : null)
                .role(user.getRole() != null ? user.getRole() : null)
                .status(user.getStatus() != null ? user.getStatus() : null)
                .build();
    }

    @Override
    public List<UserResponse> toResponses(List<User> users) {
        if (users == null) return null;
        return users.stream().map(this::toResponse).toList();
    }

    @Override
    public PageResponse<UserResponse> toResponsePage(Page<User> users) {
        if (users == null) return null;
        var responsePage = users.map(this::toResponse);
        return PageResponse.from(responsePage);
    }
}
