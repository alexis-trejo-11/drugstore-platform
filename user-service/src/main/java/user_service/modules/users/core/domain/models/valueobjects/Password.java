package user_service.modules.users.core.domain.models.valueobjects;

import org.springframework.security.crypto.bcrypt.BCrypt;

public record Password(String value) {
    public Password {
        if (value == null || value.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 chars");
        }
    }

    public String hash() {
        return BCrypt.hashpw(value, BCrypt.gensalt());
    }
}
