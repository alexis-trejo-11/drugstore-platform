package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects;

import org.springframework.security.crypto.bcrypt.BCrypt;

public record Password(String value) {
    public Password {
        if (value == null || value.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 chars");
        }
    }

		public static final Password DEFAULT_OAUTH_PASSWORD = new Password("oauth2userdefaultpassword");

    public String hash() {
        return BCrypt.hashpw(value, BCrypt.gensalt());
    }
}
