package user_service.modules.auth.core.domain.session;

import java.util.Map;

public record UserClaims(
        String userId,
        String email,
        String role) {

    public static UserClaims accessClaims(String userId) {
        return new UserClaims(userId, "", "");
    }

    public Map<String, Object> toMap() {
        return Map.of(
                "userId", userId,
                "email", email,
                "role", role);
    }
}
