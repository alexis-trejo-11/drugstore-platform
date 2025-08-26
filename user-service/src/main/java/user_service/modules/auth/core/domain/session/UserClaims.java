package user_service.modules.auth.core.domain.session;

import java.util.Map;

import user_service.modules.users.core.domain.models.entities.User;

public record UserClaims(
        String userId,
        String email,
        String role) {

    public static UserClaims from(User user) {
        return new UserClaims(
                user.getId().toString(),
                user.getEmail(),
                user.getRole().getRoleName());
    }

    public Map<String, Object> toMap() {
        return Map.of(
                "userId", userId,
                "email", email,
                "role", role);
    }
}
