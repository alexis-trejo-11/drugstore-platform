package microservice.auth.app.core.models;

import lombok.Builder;
import lombok.Getter;
import microservice.auth.app.core.valueobjects.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Getter
@Builder
public class User {
    private UserId id;
    private Email email;
    private PhoneNumber phoneNumber;
    private Password password;
    private UserRole role;
    private LocalDateTime joinedAt;
    private LocalDateTime lastLoginAt;
    private boolean isActive = true;
    private boolean hasTwoFactorAuthEnabled = false;

    protected User(UserId id, Email email, PhoneNumber phoneNumber, Password password, UserRole role) {
        this.id = Objects.requireNonNull(id);
        this.email = Objects.requireNonNull(email);
        this.password = Objects.requireNonNull(password);
        this.role = Objects.requireNonNull(role);
        this.joinedAt = LocalDateTime.now();
        this.phoneNumber = phoneNumber;
        this.lastLoginAt = LocalDateTime.now();
    }

    public static User create(Email email, PhoneNumber phoneNumber, Password password, UserRole role) {
        return new User(UserId.generate(), email, phoneNumber ,password , role);
    }


    public void updateEmail(Email newEmail) {
        this.email = newEmail;
    }

    public void changePassword(Password newPassword) {
        this.password = newPassword;
    }
}

