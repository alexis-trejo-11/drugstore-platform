package microservice.user_service.users.core.domain.service;

import microservice.user_service.users.core.domain.exceptions.WeakPlainPasswordError;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {

    private static final String PASSWORD_REGEX =
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>/?]).{8,}$";

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    public static void validatePasswordStrength(String password) {
        if (password == null || password.isBlank()) {
            throw new WeakPlainPasswordError("Password cannot be null or empty.");
        }

        Matcher matcher = PASSWORD_PATTERN.matcher(password);

        if (!matcher.matches()) {
            throw new WeakPlainPasswordError("Password is not strong enough. It must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character.");
        }


    }
}
