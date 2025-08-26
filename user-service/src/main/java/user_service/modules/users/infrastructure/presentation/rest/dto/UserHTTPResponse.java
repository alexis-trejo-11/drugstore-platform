package user_service.modules.users.infrastructure.presentation.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import user_service.modules.users.core.application.dto.UserResponse;
import user_service.modules.users.core.domain.models.enums.UserRole;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserHTTPResponse {
    private UUID id;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private String joinedAt;
    private String lastLoginAt;

    public static UserHTTPResponse from(UserResponse user) {
        return UserHTTPResponse.builder()
                .id(user.id())
                .email(user.email())
                .phoneNumber(user.phoneNumber())
                .role(user.role())
                .joinedAt(user.joinedAt())
                .lastLoginAt(user.lastLoginAt())
                .build();
    }
}
