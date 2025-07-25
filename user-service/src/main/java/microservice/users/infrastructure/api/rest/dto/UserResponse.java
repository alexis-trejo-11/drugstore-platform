package microservice.users.infrastructure.api.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.users.core.models.User;
import microservice.users.core.models.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String email;
    private UserRole role;
    private LocalDateTime joinedAt;
    private LocalDateTime lastLoginAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId().value())
                .email(user.getEmail().value())
                .role(user.getRole())
                .joinedAt(user.getJoinedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
