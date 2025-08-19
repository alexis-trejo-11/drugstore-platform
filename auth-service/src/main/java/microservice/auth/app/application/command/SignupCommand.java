package microservice.auth.app.application.command;

import lombok.Getter;
import microservice.auth.app.core.valueobjects.Email;
import microservice.auth.app.core.valueobjects.Password;
import microservice.auth.app.core.valueobjects.PhoneNumber;
import microservice.auth.app.core.valueobjects.UserRole;

@Getter
public class SignupCommand {
    private final Email email;
    private final PhoneNumber phone;
    private final Password password;
    private final UserRole role;

    public SignupCommand(Email email, PhoneNumber phone, Password password, UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        if (email == null || email.value().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (phone == null || phone.value().trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be empty");
        }
        if (password == null || password.value().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

}
